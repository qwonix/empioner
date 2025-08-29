package ru.qwonix.empioner.telegram.bot.telegram.callback.handler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.SeriesCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramSeasonService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramSeriesService;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class SeriesCallbackDataHandler implements CallbackDataHandler {

    private final TelegramSeasonService telegramSeasonService;
    private final TelegramSeriesService telegramSeriesService;
    private final TelegramBotExecutionService telegramBotExecutionService;

    @Override
    public boolean canHandle(Class<? extends CallbackData> callbackData) {
        return SeriesCallbackData.class.isAssignableFrom(callbackData);
    }

    @Override
    public void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData callbackData) {
        SeriesCallbackData seriesCallbackData = (SeriesCallbackData) callbackData;
        Optional<Series> optionalSeries = telegramSeriesService.findById(seriesCallbackData.seriesId());

        if (optionalSeries.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Такого сериала не существует. Попробуйте найти его заново.",
                    true
            );
            return;
        }
        Series series = optionalSeries.get();
        List<Season> seasons = telegramSeasonService.findAll(series);
        if (seasons.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Ни одного сезона не найдено. Попробуйте заново немного позже.",
                    true
            );
            return;
        }

        InlineKeyboardMarkup keyboard = telegramSeriesService.createKeyboard(series, seriesCallbackData.page());
        String text = telegramSeriesService.createText(series);

        telegramBotExecutionService.send(user, text, keyboard, series.imageId());
    }
}