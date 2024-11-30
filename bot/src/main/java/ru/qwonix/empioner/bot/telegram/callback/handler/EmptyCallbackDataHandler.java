package ru.qwonix.empioner.bot.telegram.callback.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.qwonix.empioner.bot.entity.TelegramBotUser;
import ru.qwonix.empioner.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.bot.telegram.callback.data.EmptyCallbackData;


@RequiredArgsConstructor
@Slf4j
@Component
public class EmptyCallbackDataHandler implements CallbackDataHandler {

    private final TelegramClient bot;

    @Override
    public boolean canHandle(Class<? extends CallbackData> callbackData) {
        return EmptyCallbackData.class.isAssignableFrom(callbackData);
    }

    @Override
    public void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData callbackData) {
        AnswerCallbackQuery query = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text("По идее эта кнопка ничего не делает!")
                .showAlert(true)
                .build();
        try {
            bot.execute(query);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}

