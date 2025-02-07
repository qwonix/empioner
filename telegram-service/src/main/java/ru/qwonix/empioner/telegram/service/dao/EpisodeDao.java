package ru.qwonix.empioner.telegram.service.dao;

import ru.qwonix.empioner.telegram.service.entity.Episode;
import ru.qwonix.empioner.telegram.service.entity.id.EpisodeId;
import ru.qwonix.empioner.telegram.service.entity.id.SeasonId;
import ru.qwonix.empioner.telegram.service.entity.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

public interface EpisodeDao {
    Optional<Episode> findById(EpisodeId id);

    Integer countAllBySeasonId(SeasonId seasonId);

    Integer countAllAvailableBySeasonId(SeasonId seasonId);

    List<Episode> findAllBySeasonIdOrderByNumberWithLimitAndPage(SeasonId seasonId, int keyboardButtonsLimit, int page);

    Optional<Episode> findByVideoGroupId(VideoGroupId id);

    Boolean changeAvailable(EpisodeId id, boolean isAvailable);
}
