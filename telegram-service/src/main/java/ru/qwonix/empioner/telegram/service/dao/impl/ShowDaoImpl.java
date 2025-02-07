package ru.qwonix.empioner.telegram.service.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.service.dao.ShowDao;
import ru.qwonix.empioner.telegram.service.entity.Show;
import ru.qwonix.empioner.telegram.service.entity.id.ShowId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ShowDaoImpl implements ShowDao {

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
