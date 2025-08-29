package ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.service.spi.SeasonSpi;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcClientSeasonSpi implements SeasonSpi {

    private final JdbcClient jdbcClient;
    private final RowMapper<Season> mapper;

    @Override
    public Optional<Season> findById(SeasonId seasonId) {
        return jdbcClient.sql("select * from season where id = :id")
                .param("id", seasonId.value())
                .query(mapper)
                .optional();
    }

    @Override
    public int countAllBySeries(SeriesId seriesId) {
        return jdbcClient.sql("select count(*) as total from season where series_id = :seriesId")
                .param("seriesId", seriesId.value())
                .query(rs -> {
                    rs.next();
                    return rs.getInt("total");
                });
    }

    @Override
    public List<Season> findAllBySeriesIdOrderByNumberWithLimitAndPage(SeriesId seriesId, int limit, int page) {
        return jdbcClient.sql("select * from season where series_id = :seriesId " +
                        "order by number " +
                        "limit :limit offset :page")
                .param("seriesId", seriesId.value())
                .param("limit", limit)
                .param("page", page)
                .query(mapper)
                .list();
    }
}
