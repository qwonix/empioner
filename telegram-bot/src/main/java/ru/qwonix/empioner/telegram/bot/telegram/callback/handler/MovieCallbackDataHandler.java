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
import ru.qwonix.empioner.telegram.bot.entity.Movie;
import ru.qwonix.empioner.telegram.bot.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.bot.entity.Video;
import ru.qwonix.empioner.telegram.bot.service.MessageService;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.MovieCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramMovieService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramVideoService;

import java.util.List;
import java.util.Optional;

import static ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService.escapeMarkdownMessage;

@RequiredArgsConstructor
@Slf4j
@Component
public class MovieCallbackDataHandler implements CallbackDataHandler {
    private final TelegramMovieService telegramMovieService;
    private final TelegramVideoService telegramVideoService;
    private final TelegramBotExecutionService telegramBotExecutionService;
    private final MessageService messageService;
    private final TelegramClient bot;

    @Override
    public boolean canHandle(Class<? extends CallbackData> callbackData) {
        return MovieCallbackData.class.isAssignableFrom(callbackData);
    }

    @Override
    public void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData rawCallbackData) {
        MovieCallbackData callbackData = (MovieCallbackData) rawCallbackData;
        Optional<Movie> optional =
                telegramMovieService.findById(callbackData.movieId());

        if (optional.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Такого спешла не существует. Попробуйте найти его заново.",
                    true
            );
            return;
        }
        Movie movie = optional.get();

        Optional<Video> optionalVideo = telegramMovieService.findVideoFor(movie);
        if (optionalVideo.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Видео не найдено. Попробуйте заново.",
                    true
            );
            return;
        }
        Video video = optionalVideo.get();

        List<Video> videos = telegramVideoService.findAllVideoInGroup(video.videoGroupId());
        videos.remove(video);

        String text = telegramMovieService.createText(movie) + '\n' + telegramVideoService.createText(video);

        InlineKeyboardMarkup keyboard = telegramMovieService.createKeyboard(movie, videos);

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