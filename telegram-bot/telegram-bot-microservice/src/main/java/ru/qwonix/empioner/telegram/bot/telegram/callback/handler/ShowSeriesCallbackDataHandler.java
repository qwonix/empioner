package ru.qwonix.empioner.telegram.bot.telegram.callback.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.ShowSeriesCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramSeriesService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramShowService;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.id.ShowId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class ShowSeriesCallbackDataHandler implements CallbackDataHandler {

    private final TelegramShowService telegramShowService;
    private final TelegramSeriesService telegramSeriesService;
    private final TelegramBotExecutionService telegramBotExecutionService;

    @Value("#{T(java.util.UUID).fromString('${bot.config.show.series.image_id}')}")
    private UUID showSeriesImageId;

    @Override
    public boolean canHandle(Class<? extends CallbackData> callbackData) {
        return ShowSeriesCallbackData.class.isAssignableFrom(callbackData);
    }

    @Override
    public void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData callbackData) {
        ShowSeriesCallbackData data = (ShowSeriesCallbackData) callbackData;
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
        ShowId showId = show.id();
        List<Series> series = telegramSeriesService.findAllByShowId(showId);

        if (series.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Ни одного сериала не найдено. Попробуйте заново немного позже.",
                    true
            );
            return;
        }

        InlineKeyboardMarkup keyboard = telegramSeriesService.createKeyboard(show, series);
        String text = telegramShowService.createText(show);
        telegramBotExecutionService.send(user, text, keyboard, new ImageId(showSeriesImageId));
    }
}
