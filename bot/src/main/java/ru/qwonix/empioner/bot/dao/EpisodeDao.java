package ru.qwonix.empioner.bot.dao;

import ru.qwonix.empioner.bot.entity.Episode;
import ru.qwonix.empioner.bot.entity.id.EpisodeId;
import ru.qwonix.empioner.bot.entity.id.SeasonId;
import ru.qwonix.empioner.bot.entity.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

public interface EpisodeDao {
    Optional<Episode> findById(EpisodeId id);

    Integer countAllBySeasonId(SeasonId seasonId);

    Integer countAllAvailableBySeasonId(SeasonId seasonId);

    List<Episode> findAllBySeasonIdOrderByNumberWithLimitAndPage(SeasonId seasonId, int keyboardButtonsLimit, int page);

    Optional<Episode> findByVideoGroupId(VideoGroupId id);
}
