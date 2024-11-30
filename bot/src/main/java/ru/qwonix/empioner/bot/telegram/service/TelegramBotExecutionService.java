package ru.qwonix.empioner.bot.telegram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.qwonix.empioner.bot.entity.Image;
import ru.qwonix.empioner.bot.entity.TelegramBotUser;
import ru.qwonix.empioner.bot.entity.id.ImageId;
import ru.qwonix.empioner.bot.service.ImageService;
import ru.qwonix.empioner.bot.service.MessageService;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class TelegramBotExecutionService {

    private final MessageService messageService;
    private final TelegramClient bot;
    private final ImageService imageService;

    @Value("#{T(java.util.UUID).fromString('${bot.config.image.placeholder}')}")
    private UUID imagePlaceholder;

    public static String escapeMarkdownMessage(String markdownMessage) {
        return markdownMessage
                .replace("-", "\\-")
                .replace("!", "\\!")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("<", "\\<")
                .replace(">", "\\>")
                .replace(".", "\\.");
    }

    public void send(TelegramBotUser user, String text, InlineKeyboardMarkup keyboard, ImageId imageId) {
        Image image = imageService.findTelegramFileIdByImageId(imageId)
                .orElseGet(() ->
                        imageService.findTelegramFileIdByImageId(new ImageId(imagePlaceholder))
                                .orElse(null)
                );

        if (messageService.hasMessageId(user)) {
            if (image != null) {
                EditMessageMedia editMedia = EditMessageMedia.builder()
                        .media(new InputMediaPhoto(image.telegramFileId().value()))
                        .chatId(user.id().value())
                        .messageId(messageService.getMessageId(user))
                        .build();
                try {
                    bot.execute(editMedia);
                } catch (TelegramApiException e) {
                    log.error("message editing error {}", user, e);
                }
            }

            EditMessageCaption editMessageCaption = EditMessageCaption.builder()
                    .caption(escapeMarkdownMessage(text))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .replyMarkup(keyboard)
                    .chatId(user.id().value())
                    .messageId(messageService.getMessageId(user))
                    .build();

            try {
                bot.execute(editMessageCaption);
            } catch (TelegramApiException e) {
                log.error("message editing error {}", user, e);
            }
        } else {
            if (image != null) {
                SendPhoto photo = SendPhoto.builder()
                        .caption(escapeMarkdownMessage(text))
                        .parseMode(ParseMode.MARKDOWNV2)
                        .photo(new InputFile(image.telegramFileId().value()))
                        .replyMarkup(keyboard)
                        .chatId(user.id().value())
                        .disableNotification(true)
                        .build();
                try {
                    Message execute = bot.execute(photo);
                    messageService.setMessageId(user, execute.getMessageId());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void confirmCallback(String callbackQueryId) {
        this.executeAlertWithText(callbackQueryId, null, null);
    }

    public void executeAlertWithText(String callbackQueryId, String text, Boolean showAlert) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .text(text)
                .showAlert(showAlert)
                .build();

        this.executeAlert(answerCallbackQuery);
    }

    public void executeAlert(AnswerCallbackQuery answerCallbackQuery) {
        try {
            bot.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            log.error("execute alert sending error ", e);
        }
    }
}
