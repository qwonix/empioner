package ru.qwonix.empioner.telegram.service.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.service.dao.SeriesDao;
import ru.qwonix.empioner.telegram.service.entity.Series;
import ru.qwonix.empioner.telegram.service.entity.id.SeriesId;
import ru.qwonix.empioner.telegram.service.entity.id.ShowId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class SeriesDaoImpl implements SeriesDao {

    private final JdbcClient jdbcClient;
    private final RowMapper<Series> mapper;

    @Override
    public Optional<Series> findById(SeriesId seriesId) {
        return jdbcClient.sql("select * from series where id = :id")
                .param("id", seriesId.value())
                .query(mapper)
                .optional();
    }

    @Override
    public List<Series> findAllByShowId(ShowId id) {
        return jdbcClient.sql("select * from series where show_id = :show_id")
                .param("show_id", id.value())
                .query(mapper)
                .list();
    }
}
