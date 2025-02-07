package ru.qwonix.empioner.telegram.service.dao;

import ru.qwonix.empioner.telegram.service.entity.Series;
import ru.qwonix.empioner.telegram.service.entity.id.SeriesId;
import ru.qwonix.empioner.telegram.service.entity.id.ShowId;

import java.util.List;
import java.util.Optional;

public interface SeriesDao {
    Optional<Series> findById(SeriesId seriesId);

    List<Series> findAllByShowId(ShowId id);
}
