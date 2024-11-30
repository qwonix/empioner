package ru.qwonix.empioner.bot.service;

import ru.qwonix.empioner.bot.entity.Season;
import ru.qwonix.empioner.bot.entity.id.SeasonId;
import ru.qwonix.empioner.bot.entity.id.SeriesId;

import java.util.List;
import java.util.Optional;

public interface SeasonService {
    Optional<Season> findById(SeasonId seasonId);

    int countAllBySeries(SeriesId seriesId);

    List<Season> findAllBySeriesIdOrderByNumberWithLimitAndPage(SeriesId seriesId, int keyboardPageSeasonsLimit, int page);
}
