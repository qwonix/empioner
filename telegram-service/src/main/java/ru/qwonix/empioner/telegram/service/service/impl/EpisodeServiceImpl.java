package ru.qwonix.empioner.telegram.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.service.dao.EpisodeDao;
import ru.qwonix.empioner.telegram.service.entity.Episode;
import ru.qwonix.empioner.telegram.service.entity.id.EpisodeId;
import ru.qwonix.empioner.telegram.service.entity.id.SeasonId;
import ru.qwonix.empioner.telegram.service.entity.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.service.EpisodeService;

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
}
