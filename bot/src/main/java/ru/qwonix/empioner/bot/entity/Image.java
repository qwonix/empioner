package ru.qwonix.empioner.bot.entity;

import ru.qwonix.empioner.bot.entity.id.ImageId;
import ru.qwonix.empioner.bot.entity.id.TelegramFileId;
import ru.qwonix.empioner.bot.entity.id.TelegramFileUniqueId;

public record Image(
        ImageId id,
        TelegramFileId telegramFileId,
        TelegramFileUniqueId telegramFileUniqueId
) {
}
