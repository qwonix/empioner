package ru.qwonix.empioner.service.entity;

import ru.qwonix.empioner.service.entity.id.EpisodeId;

public record EpisodeDetails(
        EpisodeId episodeId,
        Integer productionCode
) {
}
