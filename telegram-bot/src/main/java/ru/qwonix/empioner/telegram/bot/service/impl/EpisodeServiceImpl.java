package ru.qwonix.empioner.telegram.bot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.bot.dao.EpisodeDao;
import ru.qwonix.empioner.telegram.bot.entity.Episode;
import ru.qwonix.empioner.telegram.bot.entity.id.EpisodeId;
import ru.qwonix.empioner.telegram.bot.entity.id.SeasonId;
import ru.qwonix.empioner.telegram.bot.entity.id.VideoGroupId;
import ru.qwonix.empioner.telegram.bot.service.EpisodeService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EpisodeServiceImpl implements EpisodeService {

    private final EpisodeDao episodeDao;

    @Override
    public Optional<Episode> findById(EpisodeId id) {
        return episodeDao.findById(id);
    }

    @Override
    public Integer countAllBySeasonId(SeasonId seasonId) {
        return episodeDao.countAllBySeasonId(seasonId);
    }

    @Override
    public int countAllAvailableBySeasonId(SeasonId seasonId) {
        return episodeDao.countAllAvailableBySeasonId(seasonId);
    }

    @Override
    public List<Episode> findAllBySeasonIdOrderByNumberWithLimitAndPage(SeasonId seasonId, int limit, int page) {
        return episodeDao.findAllBySeasonIdOrderByNumberWithLimitAndPage(seasonId, limit, page);
    }

    @Override
    public Optional<Episode> findByVideoGroupId(VideoGroupId id) {
        return episodeDao.findByVideoGroupId(id);
    }

    @Override
    public Boolean makeAvailable(EpisodeId id) {
        return episodeDao.changeAvailable(id, Boolean.TRUE);
    }
    @Override
    public Boolean makeNotAvailable(EpisodeId id) {
        return episodeDao.changeAvailable(id, Boolean.FALSE);
    }
}
