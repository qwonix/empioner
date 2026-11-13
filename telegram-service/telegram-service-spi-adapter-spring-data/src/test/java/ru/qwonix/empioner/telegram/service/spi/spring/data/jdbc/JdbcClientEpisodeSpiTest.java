package ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc;

import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.entity.VideoDetails;
import ru.qwonix.empioner.telegram.id.VideoId;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.service.spi.VideoSpi;
import ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc.mapper.VideoRowMapper;
import ru.qwonix.empioner.telegram.exception.EpisodeNotFoundException;
import ru.qwonix.empioner.telegram.exception.VideoNotFoundException;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.spi.EpisodeSpi;
import ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc.mapper.EpisodeRowMapper;

import javax.sql.DataSource;
import ru.qwonix.empioner.telegram.exception.SeasonNotFoundException;
import ru.qwonix.empioner.telegram.exception.EpisodeAlreadyExistsException;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class JdbcClientEpisodeSpiTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");
    static AnnotationConfigApplicationContext context;
    EpisodeSpi episodes;
    JdbcClient jdbc;
    UUID season;

    @BeforeAll
    static void start() throws Exception {
        context = new AnnotationConfigApplicationContext(Config.class);
        SpringLiquibase migration = new SpringLiquibase();
        migration.setDataSource(context.getBean(DataSource.class));
        migration.setChangeLog("classpath:db/changelog/liquibase-changelog-master.xml");
        migration.afterPropertiesSet();
    }

    @AfterAll
    static void stop() {
        if (context != null) context.close();
    }

    @BeforeEach
    void setup() {
        episodes = context.getBean(EpisodeSpi.class);
        jdbc = context.getBean(JdbcClient.class);
        jdbc.sql("truncate episode, video_group, season, series, show cascade").update();
        UUID show = UUID.randomUUID();
        UUID series = UUID.randomUUID();
        season = UUID.randomUUID();
        jdbc.sql("insert into show (id,title,priority) values (:id,'Show',1)").param("id", show).update();
        jdbc.sql("insert into series (id,title,show_id,priority) values (:id,'Series',:show,1)")
                .param("id", series).param("show", show).update();
        jdbc.sql("insert into season (id,number,series_id) values (:id,1,:series)")
                .param("id", season).param("series", series).update();
    }

    @Test
    void createsUnavailableEpisodeAndEmptyVideoGroup() {
        Episode draft = episode(season, 1, "Pilot");
        Episode result = episodes.create(draft);
        assertEquals(draft, result);
        assertEquals(1, jdbc.sql("select count(*) from video_group where id=:id")
                .param("id", draft.videoGroupId().value()).query(Integer.class).single());
        assertEquals(0, jdbc.sql("select count(*) from video where video_group_id=:id")
                .param("id", draft.videoGroupId().value()).query(Integer.class).single());
        assertTrue(episodes.findById(draft.id()).isEmpty());
    }

    @Test
    void missingSeasonAndDuplicateNumberDoNotCreateExtraGroups() {
        assertThrows(SeasonNotFoundException.class, () -> episodes.create(episode(UUID.randomUUID(), 1, "Missing")));
        episodes.create(episode(season, 1, "Pilot"));
        assertThrows(EpisodeAlreadyExistsException.class, () -> episodes.create(episode(season, 1, "Duplicate")));
        assertEquals(1, jdbc.sql("select count(*) from video_group").query(Integer.class).single());
    }

    @Test
    void failedInsertRollsBackVideoGroup() {
        assertThrows(RuntimeException.class, () -> episodes.create(episode(season, 1, null)));
        assertEquals(0, jdbc.sql("select count(*) from video_group").query(Integer.class).single());
    }

    @Test
    void concurrentRequestsForSameNumberCreateOnlyOneEpisode() throws Exception {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            CountDownLatch ready = new CountDownLatch(2);
            CountDownLatch go = new CountDownLatch(1);
            Callable<Boolean> create = () -> {
                ready.countDown();
                go.await();
                try { episodes.create(episode(season, 2, "Episode")); return true; }
                catch (EpisodeAlreadyExistsException expected) { return false; }
            };
            Future<Boolean> first = executor.submit(create);
            Future<Boolean> second = executor.submit(create);
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            go.countDown();
            assertNotEquals(first.get(20, TimeUnit.SECONDS), second.get(20, TimeUnit.SECONDS));
            assertEquals(1, jdbc.sql("select count(*) from episode").query(Integer.class).single());
            assertEquals(1, jdbc.sql("select count(*) from video_group").query(Integer.class).single());
        }
    }

    @Test
    void publishingAndUnpublishingControlVisibilityWithoutChangingLinks() {
        Episode first = episodes.create(episode(season, 1, "First"));
        Episode second = episodes.create(episode(season, 2, "Second"));
        jdbc.sql("update episode set next_episode_id = :next where id = :id")
                .param("next", second.id().value()).param("id", first.id().value()).update();
        jdbc.sql("update episode set previous_episode_id = :prev where id = :id")
                .param("prev", first.id().value()).param("id", second.id().value()).update();

        assertTrue(episodes.changeAvailable(first.id(), true));
        assertTrue(episodes.changeAvailable(first.id(), true));
        assertEquals(first.id(), episodes.findById(first.id()).orElseThrow().id());
        assertEquals(1, episodes.findAllBySeasonIdOrderByNumberWithLimitAndPage(new SeasonId(season), 10, 0).size());
        assertEquals(second.id(), episodes.findById(first.id()).orElseThrow().nextEpisodeId());
        assertTrue(episodes.changeAvailable(first.id(), false));
        assertTrue(episodes.changeAvailable(first.id(), false));
        assertTrue(episodes.findById(first.id()).isEmpty());
        assertTrue(episodes.findAllBySeasonIdOrderByNumberWithLimitAndPage(new SeasonId(season), 10, 0).isEmpty());
        assertEquals(first.id().value(), jdbc.sql("select previous_episode_id from episode where id = :id")
                .param("id", second.id().value()).query(UUID.class).single());
        assertEquals(second.id().value(), jdbc.sql("select next_episode_id from episode where id = :id")
                .param("id", first.id().value()).query(UUID.class).single());
        assertFalse(episodes.changeAvailable(new EpisodeId(UUID.randomUUID()), true));
        assertFalse(episodes.changeAvailable(new EpisodeId(UUID.randomUUID()), false));
    }

    @Test
    void addsMultipleVideoVariantsAndUpdatesOneWithoutChangingItsGroup() {
        Episode episode = episodes.create(episode(season, 1, "Pilot"));
        VideoSpi videos = context.getBean(VideoSpi.class);
        Video low = videos.createForEpisode(episode.id(), video("720p", 1, true), null);
        Video high = videos.createForEpisode(episode.id(), video("1080p", 2, false),
                new VideoDetails(null, 1920, 1080, 100, "video/mp4", 3_000_000_000L, "episode.mp4"));
        assertNotEquals(low.id(), high.id());
        assertEquals(episode.videoGroupId(), low.videoGroupId());
        assertEquals(low.videoGroupId(), high.videoGroupId());
        assertEquals(2, videos.findAllByVideoGroupId(episode.videoGroupId()).size());
        assertEquals(low.id(), videos.findMaxPriorityInGroup(episode.videoGroupId()).orElseThrow().id());
        assertEquals(3_000_000_000L, videos.findDetails(high.id()).orElseThrow().fileSize());
        Video patch = new Video(high.id(), null, "4K", null, null, 3, true);
        Video updated = videos.update(high.id(), patch,
                new VideoDetails(high.id(), 3840, 2160, 100, "video/mp4", 4_000_000_000L, "4k.mp4"));
        assertEquals("4K", updated.description());
        assertEquals(high.telegramFileId(), updated.telegramFileId());
        assertEquals(episode.videoGroupId(), updated.videoGroupId());
        assertEquals(high.id(), videos.findMaxPriorityInGroup(episode.videoGroupId()).orElseThrow().id());
        assertEquals(high.id(), videos.findAllByVideoGroupId(episode.videoGroupId()).getFirst().id());
        assertEquals(4_000_000_000L, videos.findDetails(high.id()).orElseThrow().fileSize());
        assertEquals("720p", videos.findById(low.id()).orElseThrow().description());
    }

    @Test
    void invalidVideoOrMetadataRollsBackAndMissingIdsAreReported() {
        Episode episode = episodes.create(episode(season, 1, "Pilot"));
        VideoSpi videos = context.getBean(VideoSpi.class);
        assertThrows(EpisodeNotFoundException.class,
                () -> videos.createForEpisode(new EpisodeId(UUID.randomUUID()), video("HD", 1, true), null));
        assertThrows(RuntimeException.class, () -> videos.createForEpisode(episode.id(), video("HD", 1, true),
                new VideoDetails(null, 1, 1, 1, null, 10L, "bad.mp4")));
        assertTrue(videos.findAllByVideoGroupId(episode.videoGroupId()).isEmpty());
        Video created = videos.createForEpisode(episode.id(), video("HD", 1, true), null);
        Video patch = new Video(created.id(), null, "new description", null, null, null, null);
        assertThrows(RuntimeException.class, () -> videos.update(created.id(), patch,
                new VideoDetails(created.id(), 1, 1, 1, null, 10L, "bad.mp4")));
        assertEquals("HD", videos.findById(created.id()).orElseThrow().description());
        assertThrows(VideoNotFoundException.class, () -> videos.update(new VideoId(UUID.randomUUID()), patch, null));
    }

    private static Video video(String description, int priority, boolean available) {
        return new Video(null, null, description, new TelegramFileId("file-" + priority),
                new TelegramFileUniqueId("unique-" + priority), priority, available);
    }

    private static Episode episode(UUID season, int number, String title) {
        return new Episode(new EpisodeId(UUID.randomUUID()), title, "Description", number, null, null,
                new VideoGroupId(UUID.randomUUID()), new SeasonId(season), null, false);
    }

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean DataSource dataSource() {
            return new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        }
        @Bean JdbcClient jdbcClient(DataSource ds) { return JdbcClient.create(ds); }
        @Bean PlatformTransactionManager transactionManager(DataSource ds) { return new DataSourceTransactionManager(ds); }
        @Bean VideoSpi videos(JdbcClient client) { return new JdbcClientVideoSpi(client, new VideoRowMapper()); }
        @Bean EpisodeSpi episodes(JdbcClient client) { return new JdbcClientEpisodeSpi(client, new EpisodeRowMapper()); }
    }
}
