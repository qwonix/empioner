package ru.qwonix.empioner.service.entity;

import ru.qwonix.empioner.service.entity.id.ImageId;
import ru.qwonix.empioner.service.entity.id.TelegramFileId;
import ru.qwonix.empioner.service.entity.id.TelegramFileUniqueId;

public record Image(
        ImageId id,
        TelegramFileId telegramFileId,
        TelegramFileUniqueId telegramFileUniqueId
) {
}
