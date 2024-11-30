package ru.qwonix.empioner.bot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.bot.dao.SeasonDao;
import ru.qwonix.empioner.bot.entity.Season;
import ru.qwonix.empioner.bot.entity.id.SeasonId;
import ru.qwonix.empioner.bot.entity.id.SeriesId;
import ru.qwonix.empioner.bot.service.SeasonService;

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
