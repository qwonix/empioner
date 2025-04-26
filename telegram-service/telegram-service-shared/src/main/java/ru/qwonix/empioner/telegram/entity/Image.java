package ru.qwonix.empioner.telegram.entity;

import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;

public record Image(
        ImageId id,
        TelegramFileId telegramFileId,
        TelegramFileUniqueId telegramFileUniqueId
) {
}
