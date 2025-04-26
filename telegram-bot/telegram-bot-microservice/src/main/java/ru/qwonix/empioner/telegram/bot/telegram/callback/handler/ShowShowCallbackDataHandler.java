package ru.qwonix.empioner.telegram.bot.telegram.callback.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.CallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.ShowShowCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramShowService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class ShowShowCallbackDataHandler implements CallbackDataHandler {

    private final TelegramShowService telegramShowService;
    private final TelegramBotExecutionService telegramBotExecutionService;

    @Value("#{T(java.util.UUID).fromString('${bot.config.show.show.image_id}')}")
    private UUID showShowImageId;

    @Override
    public boolean canHandle(Class<? extends CallbackData> callbackData) {
        return ShowShowCallbackData.class.isAssignableFrom(callbackData);
    }

    @Override
    public void handle(TelegramBotUser user, CallbackQuery callbackQuery, CallbackData callbackData) {
        ShowShowCallbackData data = (ShowShowCallbackData) callbackData;

        List<Show> shows = telegramShowService.findAllForPage(data.page());
        if (shows.isEmpty()) {
            telegramBotExecutionService.executeAlertWithText(
                    callbackQuery.getId(),
                    "Странно, но ни одного шоу не было найдено. Попробуйте позже!",
                    true
            );
            return;
        }

        InlineKeyboardMarkup keyboard = telegramShowService.createKeyboard(shows);
        String text = telegramShowService.createText(shows);

        telegramBotExecutionService.send(user, text, keyboard, new ImageId(showShowImageId));
        telegramBotExecutionService.confirmCallback(callbackQuery.getId());
    }
}
