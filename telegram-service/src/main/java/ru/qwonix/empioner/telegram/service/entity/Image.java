package ru.qwonix.empioner.telegram.service.entity;

import ru.qwonix.empioner.telegram.service.entity.id.ImageId;
import ru.qwonix.empioner.telegram.service.entity.id.TelegramFileId;
import ru.qwonix.empioner.telegram.service.entity.id.TelegramFileUniqueId;

public record Image(
        ImageId id,
        TelegramFileId telegramFileId,
        TelegramFileUniqueId telegramFileUniqueId
) {
}
