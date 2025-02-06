package ru.qwonix.empioner.service.entity;

import ru.qwonix.empioner.service.entity.id.ImageId;
import ru.qwonix.empioner.service.entity.id.SeasonId;
import ru.qwonix.empioner.service.entity.id.SeriesId;

public record Season(
        SeasonId id,
        String description,
        Integer number,
        SeriesId seriesId,
        ImageId imageId,
        Boolean isAvailable
) {
}
