package ru.qwonix.empioner.service.entity;

import ru.qwonix.empioner.service.entity.id.TelegramFileId;
import ru.qwonix.empioner.service.entity.id.TelegramFileUniqueId;
import ru.qwonix.empioner.service.entity.id.VideoGroupId;
import ru.qwonix.empioner.service.entity.id.VideoId;

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
