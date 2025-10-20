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

import java.util.List;
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

            log.atDebug()
                    .addKeyValue("callbackDataType", callbackData.getClass().getSimpleName())
                    .addKeyValue("callbackQueryId", update.getCallbackQuery().getId())
                    .log("Successfully extracted callback data from update");

            return new Callback(update.getCallbackQuery(), callbackData);
        } catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .addKeyValue("callbackQueryId", update.hasCallbackQuery() ? update.getCallbackQuery().getId() : null)
                    .log("Failed to extract callback data from update");
            return null;
        }
    }

    private void onCallback(Callback result, TelegramBotUser user) {
        Class<? extends CallbackData> dataType = result.callbackData().getClass();

        log.atInfo()
                .addKeyValue("callbackDataType", dataType.getSimpleName())
                .log("Processing callback");

        List<CallbackDataHandler> matchingHandlers = callbackHandlers.stream()
                .filter(handler -> {
                    boolean canHandle = handler.canHandle(dataType);
                    log.atTrace()
                            .addKeyValue("handler", handler.getClass().getSimpleName())
                            .addKeyValue("canHandle", canHandle)
                            .log("Checking handler compatibility");
                    return canHandle;
                })
                .toList();

        if (matchingHandlers.isEmpty()) {
            log.atWarn()
                    .addKeyValue("callbackDataType", dataType.getSimpleName())
                    .log("No matching handler found for callback data");
            return;
        }

        matchingHandlers.forEach(handler -> {
            try {
                log.atInfo()
                        .addKeyValue("handler", handler.getClass().getSimpleName())
                        .addKeyValue("callbackDataType", dataType.getSimpleName())
                        .log("Invoking handler");

                handler.handle(user, result.callbackQuery(), result.callbackData());

                log.atDebug()
                        .addKeyValue("handler", handler.getClass().getSimpleName())
                        .log("Handler executed successfully");
            } catch (Exception e) {
                log.atError()
                        .setCause(e)
                        .addKeyValue("handler", handler.getClass().getSimpleName())
                        .addKeyValue("callbackDataType", dataType.getSimpleName())
                        .log("Error while executing callback handler");
            }
        });

        log.atInfo()
                .addKeyValue("callbackDataType", dataType.getSimpleName())
                .log("Callback processing finished");
    }

    public void onCallback(Update update, TelegramBotUser user) {
        log.atDebug()
                .log("Received update with callback query");

        Callback result = this.extractCallbackDataFromUpdate(update);
        if (result == null) {
            log.atWarn()
                    .log("Cannot extract callback data from update — skipping");
            return;
        }

        this.onCallback(result, user);
    }

    private record Callback(CallbackQuery callbackQuery, CallbackData callbackData) {
    }
}
