package ru.qwonix.empioner.telegram.entity;


import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

public record TelegramBotUser(
        TelegramBotUserId id,
        boolean admin
) {
    public TelegramBotUser(TelegramBotUserId id) {
        this(id, false);
    }
}
