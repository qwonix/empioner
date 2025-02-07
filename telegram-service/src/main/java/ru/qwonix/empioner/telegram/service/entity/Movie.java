package ru.qwonix.empioner.telegram.service.entity;

import ru.qwonix.empioner.telegram.service.entity.id.ImageId;
import ru.qwonix.empioner.telegram.service.entity.id.MovieId;
import ru.qwonix.empioner.telegram.service.entity.id.ShowId;
import ru.qwonix.empioner.telegram.service.entity.id.VideoGroupId;

public record Movie(
        MovieId id,
        String title,
        String description,
        ShowId showId,
        ImageId imageId,
        VideoGroupId videoGroupId,
        Integer priority,
        Boolean isAvailable

) {
}
