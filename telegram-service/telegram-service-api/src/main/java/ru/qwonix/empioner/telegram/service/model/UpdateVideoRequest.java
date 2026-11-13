package ru.qwonix.empioner.telegram.service.model;

import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;

public record UpdateVideoRequest(String description, TelegramFileId telegramFileId,
                                 TelegramFileUniqueId telegramFileUniqueId, Integer priority,
                                 Boolean isAvailable, VideoMetadataRequest details) {
    public UpdateVideoRequest {
        EpisodeVideoRequest.validate(description, telegramFileId, telegramFileUniqueId);
        if (description == null && telegramFileId == null && telegramFileUniqueId == null && priority == null && isAvailable == null && details == null)
            throw new IllegalArgumentException("Provide at least one video field to update");
    }
}
