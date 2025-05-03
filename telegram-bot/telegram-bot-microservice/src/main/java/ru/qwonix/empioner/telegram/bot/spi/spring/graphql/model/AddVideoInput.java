package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.model;

import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;

public record AddVideoInput(TelegramFileId telegramFileId,
                            TelegramFileUniqueId telegramFileUniqueId,
                            Integer width,
                            Integer height,
                            Integer duration,
                            String mimeType,
                            Integer fileSize,
                            String fileName) {
}
