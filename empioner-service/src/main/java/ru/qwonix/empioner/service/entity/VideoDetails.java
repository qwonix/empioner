package ru.qwonix.empioner.service.entity;

import ru.qwonix.empioner.service.entity.id.VideoId;

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
