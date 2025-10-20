package ru.qwonix.empioner.telegram.bot.telegram;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.qwonix.empioner.telegram.bot.api.TelegramBotUserApi;
import ru.qwonix.empioner.telegram.bot.telegram.handler.UpdateHandler;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;

import java.util.UUID;

import static ru.qwonix.empioner.telegram.bot.config.Metrics.TELEGRAM_UPDATES_TOTAL;

@Slf4j
@Component
public class MoviePlayerBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramBotUserApi telegramBotUserApi;
    private final UpdateHandler updateHandler;
    private final MeterRegistry meterRegistry;

    public MoviePlayerBot(TelegramBotUserApi telegramBotUserApi,
                          @Lazy UpdateHandler updateHandler,
                          MeterRegistry meterRegistry) {
        this.telegramBotUserApi = telegramBotUserApi;
        this.updateHandler = updateHandler;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void consume(Update update) {
        meterRegistry.counter(TELEGRAM_UPDATES_TOTAL.getName(),
                "status", "success"
        ).increment();

        MDC.put("traceId", UUID.randomUUID().toString());
        MDC.put("updateId", String.valueOf(update.getUpdateId()));

        log.atInfo().log("Received new update");

        try {
            User telegramUser = extractTelegramUser(update);
            MDC.put("userId", String.valueOf(telegramUser.getId()));

            log.atDebug()
                    .addKeyValue("userId", telegramUser.getId())
                    .addKeyValue("userName", telegramUser.getUserName())
                    .addKeyValue("firstName", telegramUser.getFirstName())
                    .log("Resolved Telegram user from update");

            TelegramBotUser user = telegramBotUserApi.merge(telegramUser);
            telegramBotUserApi.addActivity(user.id());

            if (update.hasCallbackQuery()) {
                log.atInfo().addKeyValue("type", "callback_query").log("Dispatching update");
                updateHandler.onCallback(update, user);

            } else if (update.hasMyChatMember()) {
                log.atInfo().addKeyValue("type", "my_chat_member").log("Dispatching update");
                updateHandler.onUserStatusChanged(update, user);

            } else if (update.hasMessage()) {
                log.atInfo().addKeyValue("type", "message").log("Dispatching update");

                Message message = update.getMessage();
                if (message.hasText()) {
                    log.atDebug().addKeyValue("contentType", "text").log("Processing message");
                    updateHandler.onText(update, user);
                } else if (message.hasVideo()) {
                    log.atDebug().addKeyValue("contentType", "video").log("Processing message");
                    updateHandler.onVideo(update, user);
                } else if (message.hasPhoto()) {
                    log.atDebug().addKeyValue("contentType", "photo").log("Processing message");
                    updateHandler.onPhoto(update, user);
                } else {
                    log.atWarn().addKeyValue("messageType", "unknown").log("Message type not supported");
                }
            } else {
                log.atWarn().log("Update does not contain message, callback or chat member event");
            }

            log.atInfo()
                    .addKeyValue("updateId", update.getUpdateId())
                    .addKeyValue("userId", telegramUser.getId())
                    .log("Update processed successfully");

        } catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .addKeyValue("updateId", update.getUpdateId())
                    .log("Error while processing update");
        } finally {
            MDC.clear();
        }
    }

    private User extractTelegramUser(Update update) {
        if (update.hasMyChatMember()) {
            return update.getMyChatMember().getFrom();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom();
        } else if (update.hasMessage()) {
            return update.getMessage().getFrom();
        }
        throw new IllegalArgumentException("Update does not contain user info");
    }
}
