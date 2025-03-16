package ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.service.spi.MovieSpi;
import ru.qwonix.empioner.telegram.entity.Movie;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcClientMovieSpi implements MovieSpi {

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
