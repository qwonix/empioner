package ru.qwonix.empioner.telegram.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.service.dao.SeriesDao;
import ru.qwonix.empioner.telegram.service.entity.Series;
import ru.qwonix.empioner.telegram.service.entity.id.SeriesId;
import ru.qwonix.empioner.telegram.service.entity.id.ShowId;
import ru.qwonix.empioner.telegram.service.service.SeriesService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SeriesServiceImpl implements SeriesService {

    private final SeriesDao seriesDao;

    @Override
    public Optional<Series> findById(SeriesId seriesId) {
        return seriesDao.findById(seriesId);
    }

    @Override
    public List<Series> findAllByShowId(ShowId id) {
        return seriesDao.findAllByShowId(id);
    }
}
