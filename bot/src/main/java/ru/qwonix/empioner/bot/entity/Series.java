package ru.qwonix.empioner.bot.entity;

import ru.qwonix.empioner.bot.entity.id.ImageId;
import ru.qwonix.empioner.bot.entity.id.SeriesId;
import ru.qwonix.empioner.bot.entity.id.ShowId;

public record Series(
        SeriesId id,
        String title,
        String description,
        ShowId showId,
        ImageId imageId,
        Integer priority,
        Boolean isAvailable

) {
}
