package ru.qwonix.empioner.telegram.bot.telegram.callback.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.ShowCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramShowService;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class ShowCallbackDataHandler implements CallbackDataHandler {

    private final TelegramShowService telegramShowService;
    private final TelegramBotExecutionService telegramBotExecutionService;

    @Override
    public boolean canHandle(Class<? extends CallbackData> callbackData) {
        return ShowCallbackData.class.isAssignableFrom(callbackData);
    }

    @Override
    public void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData callbackData) {
        ShowCallbackData showCallbackData = (ShowCallbackData) callbackData;
        Optional<Show> optionalShow = telegramShowService.findById(showCallbackData.showId());

        if (optionalShow.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Такого шоу не существует. Попробуйте найти его заново.",
                    true
            );
            return;
        }
        Show show = optionalShow.get();

        InlineKeyboardMarkup keyboard = telegramShowService.createKeyboard(show);
        String text = telegramShowService.createText();
        telegramBotExecutionService.send(user, text, keyboard, show.imageId());
    }
}
