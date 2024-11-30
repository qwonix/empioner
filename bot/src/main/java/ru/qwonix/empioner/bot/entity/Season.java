package ru.qwonix.empioner.bot.entity;

import ru.qwonix.empioner.bot.entity.id.ImageId;
import ru.qwonix.empioner.bot.entity.id.SeasonId;
import ru.qwonix.empioner.bot.entity.id.SeriesId;

public record Season(
        SeasonId id,
        String description,
        Integer number,
        SeriesId seriesId,
        ImageId imageId,
        Boolean isAvailable
) {
}
