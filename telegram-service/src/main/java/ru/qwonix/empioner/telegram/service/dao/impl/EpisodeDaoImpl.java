package ru.qwonix.empioner.telegram.service.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.service.dao.EpisodeDao;
import ru.qwonix.empioner.telegram.service.entity.Episode;
import ru.qwonix.empioner.telegram.service.entity.id.EpisodeId;
import ru.qwonix.empioner.telegram.service.entity.id.SeasonId;
import ru.qwonix.empioner.telegram.service.entity.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class EpisodeDaoImpl implements EpisodeDao {

    private final JdbcClient jdbcClient;
    private final RowMapper<Episode> mapper;

    @Override
    public Optional<Episode> findById(EpisodeId episodeId) {
        return jdbcClient.sql("select * from episode where id = :id")
                .param("id", episodeId.value())
                .query(mapper)
                .optional();
    }

    @Override
    public Integer countAllBySeasonId(SeasonId seasonId) {
        return jdbcClient.sql("select count(*) as total from episode where season_id = :seasonId")
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
        return jdbcClient.sql("select * from episode where season_id = :season_id " +
                              "order by number " +
                              "limit :limit offset :page")
                .param("season_id", seasonId.value())
                .param("limit", limit)
                .param("page", page * limit)
                .query(mapper)
                .list();
    }

    @Override
    public Optional<Episode> findByVideoGroupId(VideoGroupId id) {
        return jdbcClient.sql("select * from episode where video_group_id = :id")
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
