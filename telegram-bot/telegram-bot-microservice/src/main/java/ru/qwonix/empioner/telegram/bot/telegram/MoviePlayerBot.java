package ru.qwonix.empioner.telegram.bot.telegram;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import ru.qwonix.empioner.telegram.bot.config.UpdateProcessingContext;
import java.time.Duration;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static ru.qwonix.empioner.telegram.bot.config.Metrics.TELEGRAM_UPDATES_TOTAL;

@Slf4j
@Component
public class MoviePlayerBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramBotUserApi telegramBotUserApi;
    private final UpdateHandler updateHandler;
    private final MeterRegistry meterRegistry;
    private final AtomicInteger inFlight = new AtomicInteger();
    private final AtomicLong startedAt = new AtomicLong();

    public MoviePlayerBot(TelegramBotUserApi telegramBotUserApi,
                          @Lazy UpdateHandler updateHandler,
                          MeterRegistry meterRegistry) {
        this.telegramBotUserApi = telegramBotUserApi;
        this.updateHandler = updateHandler;
        this.meterRegistry = meterRegistry;
        meterRegistry.gauge("empioner.worker.inflight", inFlight);
        meterRegistry.gauge("empioner.worker.started.timestamp.seconds", startedAt);
        for (String type : new String[]{"text", "video", "photo", "callback_query", "my_chat_member", "other"}) {
            meterRegistry.counter("empioner.updates.received", "type", type);
            for (String outcome : new String[]{"success", "error", "partial", "ignored"}) {
                meterRegistry.counter("empioner.updates.processed", "type", type, "outcome", outcome);
            }
        }
    }

    @Override
    public void consume(Update update) {
        String type = updateType(update);
        String outcome = "error";
        meterRegistry.counter("empioner.updates.received", "type", type).increment();
        Timer.Sample sample = Timer.start(meterRegistry);
        UpdateProcessingContext.begin();
        startedAt.set(System.currentTimeMillis() / 1000);
        inFlight.incrementAndGet();

        MDC.put("traceId", UUID.randomUUID().toString());
        MDC.put("updateId", String.valueOf(update.getUpdateId()));

        log.atInfo().log("Received new update");

        try {
            if (type.equals("other") || type.equals("photo")) {
                outcome = "ignored";
                return;
            }
            User telegramUser = extractTelegramUser(update);
            MDC.put("userId", String.valueOf(telegramUser.getId()));

            log.atDebug()
                    .addKeyValue("userId", telegramUser.getId())
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
                    .log("Update processing finished");
            outcome = UpdateProcessingContext.hasFailure() ? "partial" : "success";

        } catch (Exception e) {
            log.atError()
                    .setCause(e)
                    .addKeyValue("updateId", update.getUpdateId())
                    .log("Error while processing update");
        } finally {
            meterRegistry.counter("empioner.updates.processed", "type", type, "outcome", outcome).increment();
            meterRegistry.counter(TELEGRAM_UPDATES_TOTAL.getName(), "status", outcome).increment();
            sample.stop(Timer.builder("empioner.update.processing")
                    .tag("type", type).tag("outcome", outcome)
                    .serviceLevelObjectives(Duration.ofMillis(100), Duration.ofMillis(250),
                            Duration.ofMillis(500), Duration.ofSeconds(1), Duration.ofSeconds(2),
                            Duration.ofSeconds(5), Duration.ofSeconds(10))
                    .register(meterRegistry));
            UpdateProcessingContext.clear();
            inFlight.decrementAndGet();
            MDC.clear();
        }
    }

    private static String updateType(Update update) {
        if (update.hasCallbackQuery()) return "callback_query";
        if (update.hasMyChatMember()) return "my_chat_member";
        if (!update.hasMessage()) return "other";
        if (update.getMessage().hasText()) return "text";
        if (update.getMessage().hasVideo()) return "video";
        if (update.getMessage().hasPhoto()) return "photo";
        return "other";
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
