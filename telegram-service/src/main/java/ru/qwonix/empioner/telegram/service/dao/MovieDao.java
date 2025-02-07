package ru.qwonix.empioner.telegram.service.dao;

import ru.qwonix.empioner.telegram.service.entity.Movie;
import ru.qwonix.empioner.telegram.service.entity.id.MovieId;
import ru.qwonix.empioner.telegram.service.entity.id.ShowId;
import ru.qwonix.empioner.telegram.service.entity.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

public interface MovieDao {
    Optional<Movie> findById(MovieId id);

    List<Movie> findAllByShowId(ShowId id);

    Optional<Movie> findByVideoId(VideoGroupId id);
}
