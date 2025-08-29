package ru.qwonix.empioner.telegram.entity;

import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;

public record Episode(
        EpisodeId id,
        String title,
        String description,
        Integer number,

        EpisodeId previousEpisodeId,
        EpisodeId nextEpisodeId,

        VideoGroupId videoGroupId,
        SeasonId seasonId,
        Boolean isAvailable
) {
}
