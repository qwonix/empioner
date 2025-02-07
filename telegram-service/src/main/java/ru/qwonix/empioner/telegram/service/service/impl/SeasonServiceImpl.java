package ru.qwonix.empioner.telegram.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.service.dao.SeasonDao;
import ru.qwonix.empioner.telegram.service.entity.Season;
import ru.qwonix.empioner.telegram.service.entity.id.SeasonId;
import ru.qwonix.empioner.telegram.service.entity.id.SeriesId;
import ru.qwonix.empioner.telegram.service.service.SeasonService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SeasonServiceImpl implements SeasonService {

    private final SeasonDao seasonDao;

    @Override
    public Optional<Season> findById(SeasonId seasonId) {
        return seasonDao.findById(seasonId);
    }

    @Override
    public int countAllBySeries(SeriesId seriesId) {
        return seasonDao.countAllBySeries(seriesId);
    }

    @Override
    public List<Season> findAllBySeriesIdOrderByNumberWithLimitAndPage(SeriesId seriesId, int limit, int page) {
        return seasonDao.findAllBySeriesIdOrderByNumberWithLimitAndPage(seriesId, limit, page);
    }
}
