package ru.qwonix.empioner.telegram.bot.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.bot.spi.EpisodeSpi;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.bot.api.EpisodeApi;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EpisodeUseCase implements EpisodeApi {

    private final EpisodeSpi episodeDao;

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
