package ru.qwonix.empioner.telegram.bot.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;

public interface UpdateHandler {

    void onVideo(Update update, TelegramBotUser user);

    void onCallback(Update update, TelegramBotUser user);

    void onPhoto(Update update, TelegramBotUser user);

    void onText(Update update, TelegramBotUser user);

    void onUserStatusChanged(Update update, TelegramBotUser user);
}
