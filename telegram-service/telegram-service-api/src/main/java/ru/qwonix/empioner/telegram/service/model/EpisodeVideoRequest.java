package ru.qwonix.empioner.telegram.service.model;

import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;

public record EpisodeVideoRequest(String description, TelegramFileId telegramFileId,
                                  TelegramFileUniqueId telegramFileUniqueId, Integer priority,
                                  Boolean isAvailable, VideoMetadataRequest details) {
    public EpisodeVideoRequest {
        validate(description, telegramFileId, telegramFileUniqueId);
        if (description == null || telegramFileId == null || telegramFileUniqueId == null || priority == null || isAvailable == null) {
            throw new IllegalArgumentException("Video description, Telegram IDs, priority and availability are required");
        }
    }

    public static void validate(String description, TelegramFileId fileId, TelegramFileUniqueId uniqueId) {
        if (description != null && description.isBlank()) throw new IllegalArgumentException("Video description must not be blank");
        if (fileId != null && (fileId.value() == null || fileId.value().isBlank() || fileId.value().length() > 100))
            throw new IllegalArgumentException("Invalid Telegram file ID (maximum 100 characters)");
        if (uniqueId != null && (uniqueId.value() == null || uniqueId.value().isBlank() || uniqueId.value().length() > 100))
            throw new IllegalArgumentException("Invalid Telegram unique file ID (maximum 100 characters)");
    }
}
