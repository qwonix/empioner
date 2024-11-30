package ru.qwonix.empioner.bot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.bot.dao.MovieDao;
import ru.qwonix.empioner.bot.entity.Movie;
import ru.qwonix.empioner.bot.entity.id.MovieId;
import ru.qwonix.empioner.bot.entity.id.ShowId;
import ru.qwonix.empioner.bot.entity.id.VideoGroupId;
import ru.qwonix.empioner.bot.service.MovieService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MovieServiceImpl implements MovieService {

    private final MovieDao movieDao;

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
