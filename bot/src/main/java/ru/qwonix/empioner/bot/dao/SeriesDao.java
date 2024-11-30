package ru.qwonix.empioner.bot.dao;

import ru.qwonix.empioner.bot.entity.Series;
import ru.qwonix.empioner.bot.entity.id.SeriesId;
import ru.qwonix.empioner.bot.entity.id.ShowId;

import java.util.List;
import java.util.Optional;

public interface SeriesDao {
    Optional<Series> findById(SeriesId seriesId);

    List<Series> findAllByShowId(ShowId id);
}
