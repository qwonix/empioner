package ru.qwonix.empioner.telegram.bot.service;

import ru.qwonix.empioner.telegram.bot.entity.Movie;
import ru.qwonix.empioner.telegram.bot.entity.id.MovieId;
import ru.qwonix.empioner.telegram.bot.entity.id.ShowId;
import ru.qwonix.empioner.telegram.bot.entity.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    Optional<Movie> findById(MovieId id);

    List<Movie> findAllByShowId(ShowId id);

    Optional<Movie> findByVideoId(VideoGroupId id);
}
