package ru.qwonix.empioner.telegram.bot.entity;

import ru.qwonix.empioner.telegram.bot.entity.id.ImageId;
import ru.qwonix.empioner.telegram.bot.entity.id.TelegramFileId;
import ru.qwonix.empioner.telegram.bot.entity.id.TelegramFileUniqueId;

public record Image(
        ImageId id,
        TelegramFileId telegramFileId,
        TelegramFileUniqueId telegramFileUniqueId
) {
}
