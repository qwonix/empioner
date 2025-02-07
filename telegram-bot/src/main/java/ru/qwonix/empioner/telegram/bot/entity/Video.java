package ru.qwonix.empioner.telegram.bot.entity;

import ru.qwonix.empioner.telegram.bot.entity.id.TelegramFileId;
import ru.qwonix.empioner.telegram.bot.entity.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.bot.entity.id.VideoGroupId;
import ru.qwonix.empioner.telegram.bot.entity.id.VideoId;

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
