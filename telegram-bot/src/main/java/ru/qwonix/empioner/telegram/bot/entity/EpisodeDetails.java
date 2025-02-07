package ru.qwonix.empioner.telegram.bot.entity;

import ru.qwonix.empioner.telegram.bot.entity.id.EpisodeId;

public record EpisodeDetails(
        EpisodeId episodeId,
        Integer productionCode
) {
}
