package ru.qwonix.empioner.bot.telegram.callback.handler;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.qwonix.empioner.bot.entity.TelegramBotUser;
import ru.qwonix.empioner.bot.telegram.callback.data.CallbackData;

public interface CallbackDataHandler {
    boolean canHandle(Class<? extends CallbackData> callbackData);

    void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData callbackData);
}
