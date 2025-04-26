package ru.qwonix.empioner.telegram.entity;

import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;

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
