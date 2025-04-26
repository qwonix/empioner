package ru.qwonix.empioner.telegram.entity;

import ru.qwonix.empioner.telegram.id.VideoId;

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
