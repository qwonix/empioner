package ru.qwonix.empioner.telegram.service.dao;

import ru.qwonix.empioner.telegram.service.entity.Season;
import ru.qwonix.empioner.telegram.service.entity.id.SeasonId;
import ru.qwonix.empioner.telegram.service.entity.id.SeriesId;

import java.util.List;
import java.util.Optional;

public interface SeasonDao {
    Optional<Season> findById(SeasonId seasonId);

    int countAllBySeries(SeriesId seriesId);

    List<Season> findAllBySeriesIdOrderByNumberWithLimitAndPage(SeriesId seriesId, int limit, int page);
}
