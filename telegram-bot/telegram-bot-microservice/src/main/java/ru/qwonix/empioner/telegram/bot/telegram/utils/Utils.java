package ru.qwonix.empioner.telegram.bot.telegram.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.EmptyCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.handler.ChatCommandHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <C extends CallbackData> List<InlineKeyboardRow> createControlButtons(
            List<Button> items,
            C previousCallbackData,
            C nextCallbackData,
            final int page,
            final int pagesCount) {
        if (pagesCount > 1) {
            if (previousCallbackData == null && nextCallbackData == null) {
                throw new IllegalArgumentException("If there is more than one page, there must be a next or previous item.");
            }
        }
        List<InlineKeyboardButton> buttons = items.stream()
                .map(item -> createCallbackButton(item.callbackData(), item.text())).toList();

        int col = 2;
        List<InlineKeyboardRow> inlineKeyboard;
        if (buttons.size() % col != 0) {
            List<InlineKeyboardButton> mainRows = buttons.subList(0, buttons.size() - buttons.size() % col);
            inlineKeyboard = arrayToMatrixByCols(mainRows, col).stream()
                    .map(InlineKeyboardRow::new)
                    .collect(Collectors.toList());

            List<InlineKeyboardButton> buttonsInLastRow = buttons.subList(buttons.size() - buttons.size() % col, buttons.size());
            inlineKeyboard.add(new InlineKeyboardRow(buttonsInLastRow));
        } else {
            List<List<InlineKeyboardButton>> lists = Utils.arrayToMatrixByCols(buttons, col);
            inlineKeyboard = lists.stream().map(InlineKeyboardRow::new).collect(Collectors.toList());
        }

        if (pagesCount > 1) {
            InlineKeyboardButton previous;
            InlineKeyboardButton next;

            if (page == ChatCommandHandler.FIRST_PAGE) {
                previous = Utils.createCallbackDataButton(new EmptyCallbackData(), "×");
            } else {
                previous = Utils.createCallbackDataButton(previousCallbackData, "‹");
            }

            if (pagesCount == page + 1) {
                next = Utils.createCallbackDataButton(new EmptyCallbackData(), "×");
            } else {
                next = Utils.createCallbackDataButton(nextCallbackData, "›");
            }

            InlineKeyboardButton current =
                    Utils.createCallbackDataButton(new EmptyCallbackData(), page + 1 + "/" + pagesCount);

            InlineKeyboardRow controlButtons = new InlineKeyboardRow(previous, current, next);
            inlineKeyboard.add(controlButtons);
        }

        return inlineKeyboard;
    }

    private static InlineKeyboardButton createCallbackButton(Object object, String text) {
        try {
            return InlineKeyboardButton.builder()
                    .callbackData(objectMapper.writeValueAsString(object))
                    .text(text).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static InlineKeyboardButton createCallbackDataButton(CallbackData callback, String text) {
        return createCallbackButton(callback, text);
    }

    public static InlineKeyboardButton createSwitchInlineQueryButton(String text) {
        return InlineKeyboardButton.builder()
                .text(text)
                .switchInlineQueryCurrentChat("")
                .build();
    }

    public static InlineKeyboardMarkup createOneRowCallbackKeyboard(List<InlineKeyboardButton> buttons) {
        return createOneRowCallbackKeyboard(buttons, ButtonOrientation.VERTICAL);
    }

    public static InlineKeyboardMarkup createOneRowCallbackKeyboard(List<InlineKeyboardButton> buttons,
                                                                    ButtonOrientation orientation) {
        return switch (orientation) {
            case HORIZONTAL -> InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(buttons))
                    .build();
            case VERTICAL -> new InlineKeyboardMarkup(buttons.stream().map(InlineKeyboardRow::new).toList());
        };
    }

    public static <T> List<List<T>> arrayToMatrixByRows(List<T> originalList, int sublistSize) {
        List<List<T>> listOfLists = new ArrayList<>();
        for (int i = 0; i < originalList.size(); i += sublistSize) {
            listOfLists.add(new ArrayList<>(originalList.subList(i, Math.min(i + sublistSize, originalList.size()))));
        }
        return listOfLists;
    }

    public static <T> List<List<T>> arrayToMatrixByCols(List<T> originalList, int columnsCount) {
        if (originalList.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<T>> listOfSublists = new ArrayList<>();
        int rowsCount = originalList.size() / columnsCount;

        if (rowsCount == 0) {
            rowsCount = 1;
        }

        for (int i = 0; i < rowsCount; i++) {
            listOfSublists.add(new ArrayList<>());
        }

        for (int i = 0; i < originalList.size(); i++) {
            listOfSublists.get(i % rowsCount).add(originalList.get(i));
        }

        return listOfSublists;
    }

    public record Button(String text, CallbackData callbackData) {

    }
}
