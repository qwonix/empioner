package ru.qwonix.empioner.telegram.entity;

import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;

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
