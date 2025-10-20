package ru.qwonix.empioner.telegram.bot.telegram.callback.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.qwonix.empioner.telegram.bot.api.MessageApi;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.DeleteMessageCallbackData;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;

@RequiredArgsConstructor
@Slf4j
@Component
public class DeleteMessageCallbackDataHandler implements CallbackDataHandler {

    private final TelegramClient bot;
    private final MessageApi messageApi;

    @Override
    public boolean canHandle(Class<? extends CallbackData> callbackData) {
        boolean canHandle = DeleteMessageCallbackData.class.isAssignableFrom(callbackData);
        log.atTrace()
                .addKeyValue("callbackData", callbackData.getSimpleName())
                .addKeyValue("canHandle", canHandle)
                .log("Checking handler compatibility");
        return canHandle;
    }

    @Override
    public void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData callbackData) {
        log.atInfo()
                .addKeyValue("userId", user.id().value())
                .addKeyValue("callbackQueryId", callbackQuery.getId())
                .log("Handling DeleteMessageCallbackData");

        if (messageApi.hasMessageId(user)) {
            Integer messageId = messageApi.getMessageId(user);
            log.atDebug()
                    .addKeyValue("userId", user.id().value())
                    .addKeyValue("messageId", messageId)
                    .log("Found messageId for user");

            DeleteMessage deleteMessage = DeleteMessage.builder()
                    .chatId(user.id().value())
                    .messageId(messageId)
                    .build();

            try {
                log.atInfo()
                        .addKeyValue("userId", user.id().value())
                        .addKeyValue("messageId", messageId)
                        .log("Attempting to delete message");
                bot.execute(deleteMessage);
                log.atInfo()
                        .addKeyValue("userId", user.id().value())
                        .addKeyValue("messageId", messageId)
                        .log("Message deleted successfully");
            } catch (TelegramApiException e) {
                log.atError()
                        .addKeyValue("userId", user.id().value())
                        .addKeyValue("messageId", messageId)
                        .setCause(e)
                        .log("Failed to delete message");
                throw new RuntimeException("Error while deleting message for user " + user.id().value(), e);
            }

            messageApi.deleteMessageId(user);
            log.atDebug()
                    .addKeyValue("userId", user.id().value())
                    .log("Removed messageId reference from MessageApi");
        } else {
            log.atWarn()
                    .addKeyValue("userId", user.id().value())
                    .log("No messageId found for user, skipping deletion");
        }
    }
}

