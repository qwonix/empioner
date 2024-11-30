package ru.qwonix.empioner.bot.entity;

import ru.qwonix.empioner.bot.entity.id.EpisodeId;

public record EpisodeDetails(
        EpisodeId episodeId,
        Integer productionCode
) {
}
