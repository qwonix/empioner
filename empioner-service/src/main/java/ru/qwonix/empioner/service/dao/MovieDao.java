package ru.qwonix.empioner.service.dao;

import ru.qwonix.empioner.service.entity.Movie;
import ru.qwonix.empioner.service.entity.id.MovieId;
import ru.qwonix.empioner.service.entity.id.ShowId;
import ru.qwonix.empioner.service.entity.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

public interface MovieDao {
    Optional<Movie> findById(MovieId id);

    List<Movie> findAllByShowId(ShowId id);

    Optional<Movie> findByVideoId(VideoGroupId id);
}
