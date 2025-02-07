package ru.qwonix.empioner.telegram.bot.entity;

import ru.qwonix.empioner.telegram.bot.entity.id.EpisodeId;
import ru.qwonix.empioner.telegram.bot.entity.id.ImageId;
import ru.qwonix.empioner.telegram.bot.entity.id.SeasonId;
import ru.qwonix.empioner.telegram.bot.entity.id.VideoGroupId;

public record Episode(
        EpisodeId id,
        String title,
        String description,
        Integer number,

        EpisodeId previousEpisodeId,
        EpisodeId nextEpisodeId,

        VideoGroupId videoGroupId,
        SeasonId seasonId,
        ImageId imageId,
        Boolean isAvailable
) {
}
