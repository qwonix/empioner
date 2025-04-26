package ru.qwonix.empioner.telegram.entity;

import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;

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
