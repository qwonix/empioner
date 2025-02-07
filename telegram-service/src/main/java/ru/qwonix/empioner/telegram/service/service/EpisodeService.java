package ru.qwonix.empioner.telegram.service.service;

import ru.qwonix.empioner.telegram.service.entity.Episode;
import ru.qwonix.empioner.telegram.service.entity.id.EpisodeId;
import ru.qwonix.empioner.telegram.service.entity.id.SeasonId;
import ru.qwonix.empioner.telegram.service.entity.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

public interface EpisodeService {
    Optional<Episode> findById(EpisodeId id);

    Integer countAllBySeasonId(SeasonId seasonId);

    int countAllAvailableBySeasonId(SeasonId seasonId);

    List<Episode> findAllBySeasonIdOrderByNumberWithLimitAndPage(SeasonId seasonId, int limit, int page);

    Optional<Episode> findByVideoGroupId(VideoGroupId id);
}
