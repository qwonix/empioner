package ru.qwonix.empioner.telegram.bot.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

public final class ObservedTelegramClient {
    private ObservedTelegramClient() { }

    public static TelegramClient wrap(TelegramClient delegate, MeterRegistry registry) {
        // Initialize each bounded series so Prometheus can observe the first event.
        for (String operation : new String[]{"send", "edit"}) {
            for (String outcome : new String[]{"success", "error"}) {
                registry.counter("empioner.video.delivery", "operation", operation, "outcome", outcome);
            }
        }
        return (TelegramClient) Proxy.newProxyInstance(TelegramClient.class.getClassLoader(),
                new Class<?>[]{TelegramClient.class}, (proxy, method, args) -> {
                    if (!method.getName().equals("execute")) {
                        try { return method.invoke(delegate, args); }
                        catch (InvocationTargetException error) { throw error.getCause(); }
                    }
                    String apiMethod = args[0].getClass().getSimpleName();
                    String videoOperation = args[0] instanceof SendVideo ? "send"
                            : args[0] instanceof EditMessageMedia edit && edit.getMedia() instanceof InputMediaVideo
                            ? "edit" : null;
                    String outcome = "success", errorType = "none";
                    Timer.Sample sample = Timer.start(registry);
                    try {
                        return method.invoke(delegate, args);
                    } catch (InvocationTargetException error) {
                        outcome = "error";
                        UpdateProcessingContext.failed();
                        Throwable cause = error.getCause();
                        errorType = cause instanceof TelegramApiRequestException apiError
                                ? "api_" + apiError.getErrorCode() : "transport";
                        throw cause;
                    } finally {
                        registry.counter("empioner.telegram.api.requests", "method", apiMethod,
                                "outcome", outcome, "error_type", errorType).increment();
                        sample.stop(registry.timer("empioner.telegram.api.request", "method", apiMethod));
                        if (videoOperation != null) {
                            registry.counter("empioner.video.delivery", "operation", videoOperation,
                                    "outcome", outcome).increment();
                        }
                    }
                });
    }
}
