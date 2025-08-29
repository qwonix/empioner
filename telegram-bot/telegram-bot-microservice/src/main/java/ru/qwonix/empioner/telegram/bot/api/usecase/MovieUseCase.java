package ru.qwonix.empioner.telegram.bot.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.bot.api.MovieApi;
import ru.qwonix.empioner.telegram.bot.spi.MovieSpi;
import ru.qwonix.empioner.telegram.entity.Movie;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MovieUseCase implements MovieApi {

    private final MovieSpi movieSpi;

    @Override
    public Optional<Movie> findById(MovieId id) {
        return movieSpi.findById(id);
    }

    @Override
    public List<Movie> findAllByShowId(ShowId id) {
        return movieSpi.findAllByShowId(id);
    }

    @Override
    public Optional<Movie> findByVideoId(VideoGroupId id) {
        return movieSpi.findByVideoId(id);
    }
}
