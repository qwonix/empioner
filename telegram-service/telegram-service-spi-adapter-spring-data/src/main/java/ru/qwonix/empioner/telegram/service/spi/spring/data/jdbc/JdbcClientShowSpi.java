package ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.service.spi.ShowSpi;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.id.ShowId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcClientShowSpi implements ShowSpi {

    private final JdbcClient jdbcClient;
    private final RowMapper<Show> mapper;

    @Override
    public Optional<Show> findById(ShowId showId) {
        return jdbcClient.sql("select * from show where id = :id")
                .param("id", showId.value())
                .query(mapper)
                .optional();
    }

    @Override
    public List<Show> findAllOrderByNumberWithLimitAndPage(int limit, int page) {
        return jdbcClient.sql("""
                        select * from show
                        limit :limit offset :page""")
                .param("limit", limit)
                .param("page", page)
                .query(mapper)
                .list();
    }
}
