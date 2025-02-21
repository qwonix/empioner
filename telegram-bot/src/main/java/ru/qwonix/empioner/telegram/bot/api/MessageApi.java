package ru.qwonix.empioner.telegram.bot.api;


import ru.qwonix.empioner.telegram.entity.TelegramBotUser;

public interface MessageApi {
    void setMessageId(TelegramBotUser user, Integer messageId);

    Integer getMessageId(TelegramBotUser user);

    boolean hasMessageId(TelegramBotUser user);

    void deleteMessageId(TelegramBotUser user);
}
