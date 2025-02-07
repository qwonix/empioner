package ru.qwonix.empioner.telegram.bot.service;

import ru.qwonix.empioner.telegram.bot.entity.Episode;
import ru.qwonix.empioner.telegram.bot.entity.id.EpisodeId;
import ru.qwonix.empioner.telegram.bot.entity.id.SeasonId;
import ru.qwonix.empioner.telegram.bot.entity.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

public interface EpisodeService {
    Optional<Episode> findById(EpisodeId id);

    Integer countAllBySeasonId(SeasonId seasonId);

    int countAllAvailableBySeasonId(SeasonId seasonId);

    List<Episode> findAllBySeasonIdOrderByNumberWithLimitAndPage(SeasonId seasonId, int limit, int page);

    Optional<Episode> findByVideoGroupId(VideoGroupId id);

    Boolean makeAvailable(EpisodeId id);

    Boolean makeNotAvailable(EpisodeId episodeId);
}
