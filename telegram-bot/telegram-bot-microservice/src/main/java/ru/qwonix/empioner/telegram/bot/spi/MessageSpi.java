package ru.qwonix.empioner.telegram.bot.spi;

import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.time.Duration;

public interface MessageSpi {
    void put(TelegramBotUserId userId, Integer value, Duration duration);

    Integer get(TelegramBotUserId userId);

    void delete(TelegramBotUserId userId);

    Boolean has(TelegramBotUserId userId);
}
