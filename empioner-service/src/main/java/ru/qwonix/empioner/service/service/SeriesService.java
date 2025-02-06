package ru.qwonix.empioner.service.service;

import ru.qwonix.empioner.service.entity.Series;
import ru.qwonix.empioner.service.entity.id.SeriesId;
import ru.qwonix.empioner.service.entity.id.ShowId;

import java.util.List;
import java.util.Optional;

public interface SeriesService {
    Optional<Series> findById(SeriesId seriesId);

    List<Series> findAllByShowId(ShowId id);
}
