package ru.qwonix.empioner.telegram.bot.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.bot.service.TelegramBotUserService;
import ru.qwonix.empioner.telegram.bot.telegram.handler.UpdateHandler;

@Slf4j
@Component
public class MoviePlayerBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramBotUserService userService;
    private final UpdateHandler updateHandler;

    public MoviePlayerBot(TelegramBotUserService userService,
                          @Lazy UpdateHandler updateHandler) {
        this.userService = userService;
        this.updateHandler = updateHandler;
    }

    @Override
    public void consume(Update update) {
        log.info("New update {}", update);
        User telegramUser;
        if (update.hasMyChatMember()) {
            telegramUser = update.getMyChatMember().getFrom();
        } else if (update.hasCallbackQuery()) {
            telegramUser = update.getCallbackQuery().getFrom();
        } else if (update.hasMessage()) {
            telegramUser = update.getMessage().getFrom();
        } else {
            throw new IllegalArgumentException("update does not contains user");
        }

        TelegramBotUser user = userService.merge(telegramUser);
        userService.addActivity(user.id());

        if (update.hasCallbackQuery()) {
            updateHandler.onCallback(update, user);
        } else if (update.hasMyChatMember()) {
            updateHandler.onUserStatusChanged(update, user);
        } else if (!update.hasMessage()) {

        } else if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                updateHandler.onText(update, user);
            } else if (update.getMessage().hasVideo()) {
                updateHandler.onVideo(update, user);
            } else if (update.getMessage().hasPhoto()) {
                updateHandler.onPhoto(update, user);
            }
        }
    }
}
