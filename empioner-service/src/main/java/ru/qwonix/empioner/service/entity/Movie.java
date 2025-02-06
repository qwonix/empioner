package ru.qwonix.empioner.service.entity;

import ru.qwonix.empioner.service.entity.id.ImageId;
import ru.qwonix.empioner.service.entity.id.MovieId;
import ru.qwonix.empioner.service.entity.id.ShowId;
import ru.qwonix.empioner.service.entity.id.VideoGroupId;

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
