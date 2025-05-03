package ru.qwonix.empioner.telegram.service.model;

import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;

public record CreateVideoRequest(TelegramFileId telegramFileId,
                                 TelegramFileUniqueId telegramFileUniqueId,
                                 Integer width,
                                 Integer height,
                                 Integer duration,
                                 String mimeType,
                                 Integer fileSize,
                                 String fileName) {
}
