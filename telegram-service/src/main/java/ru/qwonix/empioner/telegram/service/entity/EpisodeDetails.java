package ru.qwonix.empioner.telegram.service.entity;

import ru.qwonix.empioner.telegram.service.entity.id.EpisodeId;

public record EpisodeDetails(
        EpisodeId episodeId,
        Integer productionCode
) {
}
