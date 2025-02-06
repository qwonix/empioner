package ru.qwonix.empioner.service.service;

import ru.qwonix.empioner.service.entity.Season;
import ru.qwonix.empioner.service.entity.id.SeasonId;
import ru.qwonix.empioner.service.entity.id.SeriesId;

import java.util.List;
import java.util.Optional;

public interface SeasonService {
    Optional<Season> findById(SeasonId seasonId);

    int countAllBySeries(SeriesId seriesId);

    List<Season> findAllBySeriesIdOrderByNumberWithLimitAndPage(SeriesId seriesId, int keyboardPageSeasonsLimit, int page);
}
