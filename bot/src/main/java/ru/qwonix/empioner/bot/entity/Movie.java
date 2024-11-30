package ru.qwonix.empioner.bot.entity;

import ru.qwonix.empioner.bot.entity.id.ImageId;
import ru.qwonix.empioner.bot.entity.id.MovieId;
import ru.qwonix.empioner.bot.entity.id.ShowId;
import ru.qwonix.empioner.bot.entity.id.VideoGroupId;

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
