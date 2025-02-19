package ru.qwonix.empioner.telegram.bot.telegram.callback.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.bot.service.MessageService;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.EpisodeVideoCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramEpisodeService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramVideoService;

import java.util.List;
import java.util.Optional;

import static ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService.escapeMarkdownMessage;

@RequiredArgsConstructor
@Slf4j
@Component
public class EpisodeVideoCallbackDataHandler implements CallbackDataHandler {
    private final TelegramEpisodeService telegramEpisodeService;
    private final TelegramVideoService telegramVideoService;
    private final TelegramBotExecutionService telegramBotExecutionService;
    private final MessageService messageService;
    private final TelegramClient bot;

    @Override
    public boolean canHandle(Class<? extends CallbackData> callbackData) {
        return EpisodeVideoCallbackData.class.isAssignableFrom(callbackData);
    }

    @Override
    public void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData callbackData) {
        EpisodeVideoCallbackData episodeVideoCallbackData = (EpisodeVideoCallbackData) callbackData;

        Optional<Video> optionalVideo = telegramVideoService.findById(episodeVideoCallbackData.videoId());

        if (optionalVideo.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Такого видео не найдено. Попробуйте найти его заново.",
                    false);
            return;
        }
        Video video = optionalVideo.get();

        List<Video> videos = telegramVideoService.findAllVideoInGroup(video.videoGroupId());
        videos.remove(video);

        Optional<Episode> optionalEpisode = telegramEpisodeService.findByVideoGroupId(video.videoGroupId());
        if (optionalEpisode.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Эпизод не найден. Попробуйте заново немного позже",
                    false);
            return;
        }
        Episode episode = optionalEpisode.get();

        String episodeText = telegramEpisodeService.createText(episode);
        String videoText = telegramVideoService.createText(video);
        String text = episodeText + '\n' + videoText;

        InlineKeyboardMarkup keyboard = telegramEpisodeService.createKeyboard(episode, videos);

        if (messageService.hasMessageId(user)) {
            Integer messageId = messageService.getMessageId(user);
            EditMessageMedia editMedia = EditMessageMedia.builder()
                    .media(InputMediaVideo.builder()
                            .media(video.telegramFileId().value())
                            .showCaptionAboveMedia(true)
                            .build())
                    .replyMarkup(keyboard)
                    .chatId(user.id().value())
                    .messageId(messageId)
                    .build();

            EditMessageCaption messageCaption = EditMessageCaption.builder()
                    .caption(escapeMarkdownMessage(text))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .replyMarkup(keyboard)
                    .chatId(user.id().value())
                    .messageId(messageId)
                    .build();

            try {
                bot.execute(editMedia);
                bot.execute(messageCaption);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else {
            SendVideo sendVideo = SendVideo.builder()
                    .caption(escapeMarkdownMessage(text))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .video(new InputFile(video.telegramFileId().value()))
                    .replyMarkup(keyboard)
                    .chatId(user.id().value())
                    .disableNotification(true)
                    .build();

            try {
                Message execute = bot.execute(sendVideo);
                messageService.setMessageId(user, execute.getMessageId());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
