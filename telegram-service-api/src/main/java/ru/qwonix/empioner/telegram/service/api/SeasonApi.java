package ru.qwonix.empioner.telegram.service.api;

import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.SeriesId;

import java.util.List;
import java.util.Optional;

public interface SeasonApi {
    Optional<Season> findById(SeasonId seasonId);

    int countAllBySeries(SeriesId seriesId);

    List<Season> findAllBySeriesIdOrderByNumberWithLimitAndPage(SeriesId seriesId, int keyboardPageSeasonsLimit, int page);
}
