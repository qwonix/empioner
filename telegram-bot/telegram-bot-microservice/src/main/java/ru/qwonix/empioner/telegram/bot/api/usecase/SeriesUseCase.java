package ru.qwonix.empioner.telegram.bot.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.bot.spi.SeriesSpi;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.bot.api.SeriesApi;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SeriesUseCase implements SeriesApi {

    private final SeriesSpi seriesDao;

    @Override
    public Optional<Series> findById(SeriesId seriesId) {
        return seriesDao.findById(seriesId);
    }

    @Override
    public List<Series> findAllByShowId(ShowId id) {
        return seriesDao.findAllByShowId(id);
    }
}
