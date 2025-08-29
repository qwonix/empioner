package ru.qwonix.empioner.telegram.service.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.service.api.SeasonApi;
import ru.qwonix.empioner.telegram.service.spi.SeasonSpi;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SeasonUseCase implements SeasonApi {

    private final SeasonSpi seasonDao;

    @Override
    public Optional<Season> findById(SeasonId seasonId) {
        return seasonDao.findById(seasonId);
    }

    @Override
    public int countAllBySeries(SeriesId seriesId) {
        return seasonDao.countAllBySeries(seriesId);
    }

    @Override
    public List<Season> findAllBySeriesIdOrderByNumberWithLimitAndPage(SeriesId seriesId, int limit, int page) {
        return seasonDao.findAllBySeriesIdOrderByNumberWithLimitAndPage(seriesId, limit, page);
    }
}
