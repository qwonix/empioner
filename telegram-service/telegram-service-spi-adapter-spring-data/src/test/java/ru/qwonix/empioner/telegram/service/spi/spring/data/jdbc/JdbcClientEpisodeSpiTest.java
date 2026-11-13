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
        @Bean EpisodeSpi episodes(JdbcClient client) { return new JdbcClientEpisodeSpi(client, new EpisodeRowMapper()); }
    }
}
