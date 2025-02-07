package ru.qwonix.empioner.telegram.service.entity;

import ru.qwonix.empioner.telegram.service.entity.id.ImageId;
import ru.qwonix.empioner.telegram.service.entity.id.ShowId;

public record Show(
        ShowId id,
        String title,
        String description,
        ImageId imageId,
        Integer priority,
        Boolean isAvailable
) {
}