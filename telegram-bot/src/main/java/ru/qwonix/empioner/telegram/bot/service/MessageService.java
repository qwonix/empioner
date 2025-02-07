package ru.qwonix.empioner.telegram.bot.service;


import ru.qwonix.empioner.telegram.bot.entity.TelegramBotUser;

public interface MessageService {
    void setMessageId(TelegramBotUser user, Integer messageId);

    Integer getMessageId(TelegramBotUser user);

    boolean hasMessageId(TelegramBotUser user);

    void deleteMessageId(TelegramBotUser user);
}
