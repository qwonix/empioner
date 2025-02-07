package ru.qwonix.empioner.telegram.bot.service;

import ru.qwonix.empioner.telegram.bot.entity.Series;
import ru.qwonix.empioner.telegram.bot.entity.id.SeriesId;
import ru.qwonix.empioner.telegram.bot.entity.id.ShowId;

import java.util.List;
import java.util.Optional;

public interface SeriesService {
    Optional<Series> findById(SeriesId seriesId);

    List<Series> findAllByShowId(ShowId id);
}
