package ru.qwonix.empioner.telegram.bot.telegram.callback.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.ShowMovieCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramMovieService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramShowService;
import ru.qwonix.empioner.telegram.entity.Movie;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.id.ImageId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class ShowMovieCallbackDataHandler implements CallbackDataHandler {

    private final TelegramShowService telegramShowService;
    private final TelegramMovieService telegramMovieService;
    private final TelegramBotExecutionService telegramBotExecutionService;

    @Value("#{T(java.util.UUID).fromString('${bot.config.show.movie.image_id}')}")
    private UUID showMovieImageId;

    @Override
    public boolean canHandle(Class<? extends CallbackData> callbackData) {
        return ShowMovieCallbackData.class.isAssignableFrom(callbackData);
    }

    @Override
    public void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData callbackData) {
        ShowMovieCallbackData data = (ShowMovieCallbackData) callbackData;
        Optional<Show> optionalShow = telegramShowService.findById(data.showId());

        if (optionalShow.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Такого шоу не существует. Попробуйте найти его заново.",
                    true
            );
            return;
        }
        Show show = optionalShow.get();
        List<Movie> movies = telegramMovieService.findAllByShowId(show.id());

        if (movies.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Ни одного спешла не найдено. Попробуйте заново немного позже.",
                    true);
            return;
        }

        InlineKeyboardMarkup keyboard = telegramMovieService.createKeyboard(show.id(), movies);
        String text = telegramShowService.createText(show);

        telegramBotExecutionService.send(user, text, keyboard, new ImageId(showMovieImageId));
    }
}
