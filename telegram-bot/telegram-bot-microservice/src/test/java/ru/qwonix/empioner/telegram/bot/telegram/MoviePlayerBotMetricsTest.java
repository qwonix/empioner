package ru.qwonix.empioner.telegram.bot.telegram;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.qwonix.empioner.telegram.bot.api.TelegramBotUserApi;
import ru.qwonix.empioner.telegram.bot.config.UpdateProcessingContext;
import ru.qwonix.empioner.telegram.bot.telegram.handler.UpdateHandler;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoviePlayerBotMetricsTest {
    final SimpleMeterRegistry registry = new SimpleMeterRegistry();
    final TelegramBotUserApi users = mock(TelegramBotUserApi.class);
    final UpdateHandler handler = mock(UpdateHandler.class);
    final MoviePlayerBot bot = new MoviePlayerBot(users, handler, registry);

    @Test
    void recordsRealOutcomesAndDoesNotLeakPartialFailureIntoNextUpdate() {
        Update update = textUpdate();
        when(users.merge(any())).thenReturn(new TelegramBotUser(new TelegramBotUserId(123L)));
        doAnswer(call -> { UpdateProcessingContext.failed(); return null; }).doNothing()
                .when(handler).onText(any(), any());
        bot.consume(update);
        bot.consume(update);
        assertEquals(1, count("partial"));
        assertEquals(1, count("success"));
        assertEquals(2, registry.get("empioner.updates.received").tag("type", "text").counter().count());
        assertEquals(2, registry.find("empioner.update.processing").timers().stream().mapToLong(t -> t.count()).sum());
    }

    @Test
    void dependencyFailureIsNeverCountedAsSuccess() {
        when(users.merge(any())).thenThrow(new IllegalStateException("dependency unavailable"));
        bot.consume(textUpdate());
        assertEquals(1, count("error"));
        assertEquals(0, count("success"));
        verifyNoInteractions(handler);
    }

    @Test
    void unsupportedUpdateIsIgnoredWithoutCallingDependencies() {
        bot.consume(new Update());
        assertEquals(1, count("ignored"));
        verifyNoInteractions(users, handler);
    }

    double count(String outcome) {
        return registry.find("empioner.updates.processed").tag("outcome", outcome).counters().stream().mapToDouble(c -> c.count()).sum();
    }

    static Update textUpdate() {
        User from = new User(123L, "Test", false);
        Message message = new Message(); message.setFrom(from); message.setText("/start");
        Update update = new Update(); update.setUpdateId(1); update.setMessage(message);
        return update;
    }
}
