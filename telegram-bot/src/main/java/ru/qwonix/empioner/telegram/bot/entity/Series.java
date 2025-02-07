package ru.qwonix.empioner.telegram.bot.entity;

import ru.qwonix.empioner.telegram.bot.entity.id.ImageId;
import ru.qwonix.empioner.telegram.bot.entity.id.SeriesId;
import ru.qwonix.empioner.telegram.bot.entity.id.ShowId;

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
