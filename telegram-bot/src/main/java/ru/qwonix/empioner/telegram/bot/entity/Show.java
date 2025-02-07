package ru.qwonix.empioner.telegram.bot.entity;

import ru.qwonix.empioner.telegram.bot.entity.id.ImageId;
import ru.qwonix.empioner.telegram.bot.entity.id.ShowId;

public record Show(
        ShowId id,
        String title,
        String description,
        ImageId imageId,
        Integer priority,
        Boolean isAvailable
) {
}