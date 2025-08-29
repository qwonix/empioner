package ru.qwonix.empioner.telegram.service.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.entity.Movie;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.api.movieApi;
import ru.qwonix.empioner.telegram.service.spi.MovieSpi;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MovieUseCase implements movieApi {

    private final MovieSpi movieDao;

    @Override
    public Optional<Movie> findById(MovieId id) {
        return movieDao.findById(id);
    }

    @Override
    public List<Movie> findAllByShowId(ShowId id) {
        return movieDao.findAllByShowId(id);
    }

    @Override
    public Optional<Movie> findByVideoId(VideoGroupId id) {
        return movieDao.findByVideoId(id);
    }
}
