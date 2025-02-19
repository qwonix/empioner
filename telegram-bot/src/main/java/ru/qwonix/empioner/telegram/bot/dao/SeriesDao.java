package ru.qwonix.empioner.telegram.bot.dao;

import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;

import java.util.List;
import java.util.Optional;

public interface SeriesDao {
    Optional<Series> findById(SeriesId seriesId);

    List<Series> findAllByShowId(ShowId id);
}
