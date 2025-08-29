package ru.qwonix.empioner.telegram.bot.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.qwonix.empioner.telegram.bot.api.ShowApi;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.*;
import ru.qwonix.empioner.telegram.bot.telegram.config.TelegramProperties;
import ru.qwonix.empioner.telegram.bot.telegram.utils.Utils;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.id.ShowId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramShowService {
    private static final int FIRST_PAGE = 0;

    private final ShowApi showApi;
    private final TelegramProperties telegramProperties;

    public Optional<Show> findById(ShowId id) {
        return showApi.findById(id);
    }

    public String createText(Show show) {
        return show.title() + '\n' + show.description();
    }

    public InlineKeyboardMarkup createKeyboard(List<Show> shows) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (Show show : shows) {
            InlineKeyboardButton callbackDataButton = Utils.createCallbackDataButton(new ShowCallbackData(show.id()), show.title());
            buttons.add(callbackDataButton);
        }
        buttons.add(Utils.createCallbackDataButton(new DeleteMessageCallbackData(), "Назад"));
        return Utils.createOneRowCallbackKeyboard(buttons);
    }

    public List<Show> findAllForPage(Integer page) {
        return showApi.findAllOrderByNumberWithLimitAndPage(telegramProperties.keyboardButtonsMax(), page);
    }

    public String createText(List<Show> shows) {
        return "Выберите шоу";
    }

    public InlineKeyboardMarkup createKeyboard(Show show) {
        ShowSeriesCallbackData showSeriesCallbackData = new ShowSeriesCallbackData(show.id(), FIRST_PAGE);
        ShowMovieCallbackData showMovieCallbackData = new ShowMovieCallbackData(show.id(), FIRST_PAGE);
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(Utils.createCallbackDataButton(showSeriesCallbackData, "Сериалы")))
                .keyboardRow(new InlineKeyboardRow(List.of(Utils.createCallbackDataButton(showMovieCallbackData, "Спешлы"))))
                .keyboardRow(new InlineKeyboardRow(List.of(
                        Utils.createCallbackDataButton(new ShowShowCallbackData(FIRST_PAGE), "Назад"),
                        Utils.createCallbackDataButton(new DeleteMessageCallbackData(), "На главную"))))
                .build();
    }

    public String createText() {
        return "Выберите вид контента:";
    }
}