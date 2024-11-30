package ru.qwonix.empioner.bot.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.bot.dao.MovieDao;
import ru.qwonix.empioner.bot.entity.Movie;
import ru.qwonix.empioner.bot.entity.id.MovieId;
import ru.qwonix.empioner.bot.entity.id.ShowId;
import ru.qwonix.empioner.bot.entity.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MovieDaoImpl implements MovieDao {

    private final JdbcClient jdbcClient;
    private final RowMapper<Movie> mapper;

    @Override
    public Optional<Movie> findById(MovieId id) {
        return jdbcClient.sql("select * from movie where id = :id")
                .param("id", id.value())
                .query(mapper)
                .optional();
    }

    @Override
    public List<Movie> findAllByShowId(ShowId showId) {
        return jdbcClient.sql("select * from movie where show_id = :show_id")
                .param("show_id", showId.value())
                .query(mapper)
                .list();
    }

    @Override
    public Optional<Movie> findByVideoId(VideoGroupId id) {
        return jdbcClient.sql("select * from movie where video_group_id = :id")
                .param("id", id.value())
                .query(mapper)
                .optional();
    }
}
