package ru.qwonix.empioner.telegram.bot.api;

import ru.qwonix.empioner.telegram.entity.Movie;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

public interface MovieApi {
    Optional<Movie> findById(MovieId id);

    List<Movie> findAllByShowId(ShowId id);

    Optional<Movie> findByVideoId(VideoGroupId id);
}
