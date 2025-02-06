package ru.qwonix.empioner.service.entity;

import ru.qwonix.empioner.service.entity.id.ImageId;
import ru.qwonix.empioner.service.entity.id.ShowId;

public record Show(
        ShowId id,
        String title,
        String description,
        ImageId imageId,
        Integer priority,
        Boolean isAvailable
) {
}