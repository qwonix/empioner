package ru.qwonix.empioner.telegram.bot.api;

import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

public interface EpisodeApi {
    Optional<Episode> findById(EpisodeId id);

    Integer countAllBySeasonId(SeasonId seasonId);

    int countAllAvailableBySeasonId(SeasonId seasonId);

    List<Episode> findAllBySeasonIdOrderByNumberWithLimitAndPage(SeasonId seasonId, int limit, int page);

    Optional<Episode> findByVideoGroupId(VideoGroupId id);

    Boolean makeAvailable(EpisodeId id);

    Boolean makeNotAvailable(EpisodeId episodeId);
}
