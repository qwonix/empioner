package ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import ru.qwonix.empioner.telegram.exception.SeasonNotFoundException;
import ru.qwonix.empioner.telegram.exception.EpisodeAlreadyExistsException;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.spi.EpisodeSpi;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcClientEpisodeSpi implements EpisodeSpi {

    // Navigation uses published episodes in published seasons of the same series.
    // UUIDs provide a deterministic order if legacy data contains duplicate numbers.
    private static final String WITH_NEIGHBOURS = """
            select e.*, previous.id as navigation_previous_episode_id, next.id as navigation_next_episode_id
            from episode e
            join season s on s.id = e.season_id
            left join lateral (
              select candidate.id from season cs join episode candidate on candidate.season_id = cs.id
              where cs.series_id = s.series_id and cs.is_available = true and candidate.is_available = true
                and (cs.number, cs.id, candidate.number, candidate.id) < (s.number, s.id, e.number, e.id)
              order by cs.number desc, cs.id desc, candidate.number desc, candidate.id desc limit 1
            ) previous on true
            left join lateral (
              select candidate.id from season cs join episode candidate on candidate.season_id = cs.id
              where cs.series_id = s.series_id and cs.is_available = true and candidate.is_available = true
                and (cs.number, cs.id, candidate.number, candidate.id) > (s.number, s.id, e.number, e.id)
              order by cs.number, cs.id, candidate.number, candidate.id limit 1
            ) next on true
            """;

    private final JdbcClient jdbcClient;
    private final RowMapper<Episode> mapper;

    @Override
    @Transactional
    public Episode create(Episode episode) {
        // Lock the season so simultaneous creates through this operation cannot reuse a number.
        boolean seasonExists = jdbcClient.sql("select id from season where id = :id for update")
                .param("id", episode.seasonId().value()).query(UUID.class).optional().isPresent();
        if (!seasonExists) {
            throw new SeasonNotFoundException();
        }
        boolean duplicate = !jdbcClient.sql("select id from episode where season_id = :seasonId and number = :number")
                .param("seasonId", episode.seasonId().value()).param("number", episode.number())
                .query(UUID.class).list().isEmpty();
        if (duplicate) {
            throw new EpisodeAlreadyExistsException();
        }
        jdbcClient.sql("insert into video_group (id) values (:id)")
                .param("id", episode.videoGroupId().value()).update();
        return jdbcClient.sql("""
                        insert into episode (id, title, description, number, season_id, video_group_id, is_available)
                        values (:id, :title, :description, :number, :seasonId, :videoGroupId, false)
                        returning *, null::uuid as navigation_previous_episode_id, null::uuid as navigation_next_episode_id
                        """)
                .param("id", episode.id().value()).param("title", episode.title())
                .param("description", episode.description()).param("number", episode.number())
                .param("seasonId", episode.seasonId().value()).param("videoGroupId", episode.videoGroupId().value())
                .query(mapper).single();
    }

    @Override
    public Optional<Episode> findById(EpisodeId episodeId) {
        return jdbcClient.sql(WITH_NEIGHBOURS + " where e.id = :id and e.is_available = true")
                .param("id", episodeId.value())
                .query(mapper)
                .optional();
    }

    @Override
    public Integer countAllBySeasonId(SeasonId seasonId) {
        return jdbcClient.sql("select count(*) as total from episode where season_id = :seasonId " +
                        "and is_available = true")
                .param("seasonId", seasonId.value())
                .query(rs -> {
                    rs.next();
                    return rs.getInt("total");
                });
    }

    @Override
    public Integer countAllAvailableBySeasonId(SeasonId seasonId) {
        return jdbcClient.sql("select count(*) as total from episode where season_id = :seasonId " +
                        "and is_available is true")
                .param("seasonId", seasonId.value())
                .query(rs -> {
                    rs.next();
                    return rs.getInt("total");
                });
    }

    @Override
    public List<Episode> findAllBySeasonIdOrderByNumberWithLimitAndPage(SeasonId seasonId,
                                                                        int limit,
                                                                        int page) {
        return jdbcClient.sql(WITH_NEIGHBOURS + " where e.season_id = :season_id " +
                        "and e.is_available = true " +
                        "order by e.number, e.id " +
                        "limit :limit offset :page")
                .param("season_id", seasonId.value())
                .param("limit", limit)
                .param("page", page * limit)
                .query(mapper)
                .list();
    }

    @Override
    public Optional<Episode> findByVideoGroupId(VideoGroupId id) {
        return jdbcClient.sql(WITH_NEIGHBOURS + " where e.video_group_id = :id and e.is_available = true")
                .param("id", id.value())
                .query(mapper)
                .optional();
    }

    @Override
    public Boolean changeAvailable(EpisodeId id, boolean isAvailable) {
        return jdbcClient.sql("update episode set is_available = :isAvailable where id = :id")
                .param("id", id.value())
                .param("isAvailable", isAvailable)
                .update() == 1;
    }
}
