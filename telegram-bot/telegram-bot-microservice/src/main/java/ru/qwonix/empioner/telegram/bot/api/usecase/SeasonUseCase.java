package ru.qwonix.empioner.telegram.bot.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.bot.api.SeasonApi;
import ru.qwonix.empioner.telegram.bot.spi.SeasonSpi;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.SeriesId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SeasonUseCase implements SeasonApi {

    private final SeasonSpi seasonSpi;

    @Override
    public Optional<Season> findById(SeasonId seasonId) {
        return seasonSpi.findById(seasonId);
    }

    @Override
    public int countAllBySeries(SeriesId seriesId) {
        return seasonSpi.countAllBySeries(seriesId);
    }

    @Override
    public List<Season> findAllBySeriesIdOrderByNumberWithLimitAndPage(SeriesId seriesId, int limit, int page) {
        return seasonSpi.findAllBySeriesIdOrderByNumberWithLimitAndPage(seriesId, limit, page);
    }
}
