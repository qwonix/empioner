package ru.qwonix.empioner.telegram.bot.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.qwonix.empioner.telegram.bot.api.SeasonApi;
import ru.qwonix.empioner.telegram.bot.api.SeriesApi;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.*;
import ru.qwonix.empioner.telegram.bot.telegram.config.TelegramProperties;
import ru.qwonix.empioner.telegram.bot.telegram.utils.Utils;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TelegramSeriesService {
    private static final int FIRST_PAGE = 0;

    private final SeasonApi seasonApi;
    private final SeriesApi seriesApi;
    private final TelegramProperties telegramProperties;

    public List<Series> findAllByShowId(ShowId id) {
        return seriesApi.findAllByShowId(id);
    }

    public Optional<Series> findById(SeriesId id) {
        return seriesApi.findById(id);
    }

    public InlineKeyboardMarkup createKeyboard(Series series, final int page) {
        final int BUTTONS_LIMIT = telegramProperties.keyboardButtonsMax();
        final int seasonsCount = seasonApi.countAllBySeries(series.id());
        final int pagesCount = (int) Math.ceil(seasonsCount / (double) BUTTONS_LIMIT);

        List<Season> seriesSeasons
                = seasonApi.findAllBySeriesIdOrderByNumberWithLimitAndPage(series.id(), BUTTONS_LIMIT, page);

        List<Utils.Button> buttons = seriesSeasons.stream().map(season -> new Utils.Button(
                        "Сезон " + season.number(),
                        new SeasonCallbackData(season.id(), FIRST_PAGE))
                )
                .toList();

        List<InlineKeyboardRow> keyboard = Utils.createControlButtons(
                buttons,
                new SeriesCallbackData(series.id(), page - 1),
                new SeriesCallbackData(series.id(), page + 1),
                page,
                pagesCount
        );

        keyboard.add(new InlineKeyboardRow(List.of(
                Utils.createCallbackDataButton(new ShowSeriesCallbackData(series.showId(), FIRST_PAGE), "Назад"),
                Utils.createCallbackDataButton(new DeleteMessageCallbackData(), "На главную"))));

        return new InlineKeyboardMarkup(keyboard);
    }

    public String createText(Series series) {
        return String.format("*%s*\n", series.title())
                + '\n'
                + String.format("_%s_", series.description());
    }

    public InlineKeyboardMarkup createKeyboard(Show show, List<Series> seriesList) {
        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();
        for (Series series : seriesList) {
            InlineKeyboardButton callbackDataButton = Utils.createCallbackDataButton(
                    new SeriesCallbackData(series.id(), FIRST_PAGE), series.title());
            InlineKeyboardRow inlineKeyboardButtons = new InlineKeyboardRow(callbackDataButton);
            keyboardRows.add(inlineKeyboardButtons);
        }
        keyboardRows.add(new InlineKeyboardRow(List.of(
                Utils.createCallbackDataButton(new ShowCallbackData(show.id()), "Назад"),
                Utils.createCallbackDataButton(new DeleteMessageCallbackData(), "На главную"))));

        return new InlineKeyboardMarkup(keyboardRows);
    }
}
