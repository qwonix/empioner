package ru.qwonix.empioner.telegram.service.entity;

import ru.qwonix.empioner.telegram.service.entity.id.EpisodeId;
import ru.qwonix.empioner.telegram.service.entity.id.ImageId;
import ru.qwonix.empioner.telegram.service.entity.id.SeasonId;
import ru.qwonix.empioner.telegram.service.entity.id.VideoGroupId;

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
