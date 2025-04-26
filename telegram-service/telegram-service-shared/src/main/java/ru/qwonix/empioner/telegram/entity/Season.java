package ru.qwonix.empioner.telegram.entity;

import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.SeriesId;

public record Season(
        SeasonId id,
        String description,
        Integer number,
        SeriesId seriesId,
        ImageId imageId,
        Boolean isAvailable
) {
}
