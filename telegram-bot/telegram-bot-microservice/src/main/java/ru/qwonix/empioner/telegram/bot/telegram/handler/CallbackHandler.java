package ru.qwonix.empioner.telegram.bot.telegram.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.handler.CallbackDataHandler;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class CallbackHandler {
    private final Set<CallbackDataHandler> callbackHandlers;
    private final ObjectMapper objectMapper;

    private Callback extractCallbackDataFromUpdate(Update update) {
        try {
            String jsonCallbackData = update.getCallbackQuery().getData();
            CallbackData callbackData = objectMapper.readValue(jsonCallbackData, CallbackData.class);
            return new Callback(update.getCallbackQuery(), callbackData);
        } catch (Exception e) {
            return null;
        }
    }

    private void onCallback(Callback result, TelegramBotUser user) {
        callbackHandlers.stream()
                .filter(handler -> handler.canHandle(result.callbackData().getClass()))
                .forEach(handler -> handler.handle(user, result.callbackQuery(), result.callbackData()));
    }

    public void onCallback(Update update, TelegramBotUser user) {
        Callback result = this.extractCallbackDataFromUpdate(update);
        if (result == null) {
            log.error("can't extract callback from update {}", update);
            return;
        }
        this.onCallback(result, user);
    }

    private record Callback(CallbackQuery callbackQuery, CallbackData callbackData) {
    }
}
