package ru.qwonix.empioner.telegram.service.entity;

import ru.qwonix.empioner.telegram.service.entity.id.TelegramFileId;
import ru.qwonix.empioner.telegram.service.entity.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.service.entity.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.entity.id.VideoId;

public record Video(
        VideoId id,
        VideoGroupId videoGroupId,
        String description,
        TelegramFileId telegramFileId,
        TelegramFileUniqueId telegramFileUniqueId,
        Integer priority,
        Boolean isAvailable
) {
}
