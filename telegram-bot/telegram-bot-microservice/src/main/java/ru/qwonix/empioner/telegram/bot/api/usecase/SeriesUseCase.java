package ru.qwonix.empioner.telegram.bot.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.bot.api.SeriesApi;
import ru.qwonix.empioner.telegram.bot.spi.SeriesSpi;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SeriesUseCase implements SeriesApi {

    private final SeriesSpi seriesSpi;

    @Override
    public Optional<Series> findById(SeriesId seriesId) {
        return seriesSpi.findById(seriesId);
    }

    @Override
    public List<Series> findAllByShowId(ShowId id) {
        return seriesSpi.findAllByShowId(id);
    }
}
