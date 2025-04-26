package ru.qwonix.empioner.telegram.entity;

import ru.qwonix.empioner.telegram.id.EpisodeId;

public record EpisodeDetails(
        EpisodeId episodeId,
        Integer productionCode
) {
}
