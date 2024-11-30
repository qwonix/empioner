package ru.qwonix.empioner.bot.dao;

import ru.qwonix.empioner.bot.entity.Season;
import ru.qwonix.empioner.bot.entity.id.SeasonId;
import ru.qwonix.empioner.bot.entity.id.SeriesId;

import java.util.List;
import java.util.Optional;

public interface SeasonDao {
    Optional<Season> findById(SeasonId seasonId);

    int countAllBySeries(SeriesId seriesId);

    List<Season> findAllBySeriesIdOrderByNumberWithLimitAndPage(SeriesId seriesId, int limit, int page);
}
