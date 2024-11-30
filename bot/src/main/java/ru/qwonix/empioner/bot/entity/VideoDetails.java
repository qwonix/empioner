package ru.qwonix.empioner.bot.entity;

import ru.qwonix.empioner.bot.entity.id.VideoId;

public record VideoDetails(
        VideoId videoId,
        Integer width,
        Integer height,
        Integer duration,
        String mimeType,
        Long fileSize,
        String fileName
) {
}
