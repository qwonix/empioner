package ru.qwonix.empioner.telegram.bot.dao;

import ru.qwonix.empioner.telegram.bot.entity.id.TelegramBotUserId;

import java.time.Duration;

public interface MessageDao {
    void put(TelegramBotUserId userId, Integer value, Duration duration);

    Integer get(TelegramBotUserId userId);

    void delete(TelegramBotUserId userId);

    Boolean has(TelegramBotUserId userId);
}
