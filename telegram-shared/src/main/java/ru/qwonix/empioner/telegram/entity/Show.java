package ru.qwonix.empioner.telegram.entity;

import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.id.ShowId;

public record Show(
        ShowId id,
        String title,
        String description,
        ImageId imageId,
        Integer priority,
        Boolean isAvailable
) {
}