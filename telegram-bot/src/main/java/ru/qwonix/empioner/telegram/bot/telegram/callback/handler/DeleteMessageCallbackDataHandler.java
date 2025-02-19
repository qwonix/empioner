package ru.qwonix.empioner.telegram.bot.telegram.callback.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.bot.service.MessageService;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.DeleteMessageCallbackData;

@RequiredArgsConstructor
@Slf4j
@Component
public class DeleteMessageCallbackDataHandler implements CallbackDataHandler {

    private final TelegramClient bot;
    private final MessageService messageService;

    @Override
    public boolean canHandle(Class<? extends CallbackData> callbackData) {
        return DeleteMessageCallbackData.class.isAssignableFrom(callbackData);
    }

    @Override
    public void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData callbackData) {
        if (messageService.hasMessageId(user)) {
            Integer messageId = messageService.getMessageId(user);
            DeleteMessage deleteMessage = DeleteMessage.builder()
                    .chatId(user.id().value())
                    .messageId(messageId).build();

            try {
                bot.execute(deleteMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            messageService.deleteMessageId(user);
        }
    }
}
