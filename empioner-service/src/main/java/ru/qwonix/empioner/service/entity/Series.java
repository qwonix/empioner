package ru.qwonix.empioner.service.entity;

import ru.qwonix.empioner.service.entity.id.ImageId;
import ru.qwonix.empioner.service.entity.id.SeriesId;
import ru.qwonix.empioner.service.entity.id.ShowId;

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
