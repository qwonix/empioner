package ru.qwonix.empioner.telegram.bot.api.usecase;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.qwonix.empioner.telegram.bot.spi.TelegramBotUserSpi;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelegramBotUserUseCaseMetricsTest {
    @Test
    void countsOnlySuccessfulNewRegistrations() {
        var spi = mock(TelegramBotUserSpi.class);
        var registry = new SimpleMeterRegistry();
        var useCase = new TelegramBotUserUseCase(spi, registry);
        var telegramUser = mock(User.class);
        when(telegramUser.getId()).thenReturn(123L);
        var user = new TelegramBotUser(new TelegramBotUserId(123L));
        when(spi.findUser(any())).thenReturn(Optional.of(user), Optional.empty());
        when(spi.registerNewUser(telegramUser)).thenReturn(user);

        assertSame(user, useCase.merge(telegramUser));
        assertEquals(0, registry.get("empioner.users.registered").counter().count());
        assertSame(user, useCase.merge(telegramUser));
        assertEquals(1, registry.get("empioner.users.registered").counter().count());

        when(spi.registerNewUser(telegramUser)).thenThrow(new IllegalStateException("failure"));
        assertThrows(IllegalStateException.class, () -> useCase.registerNewUser(telegramUser));
        assertEquals(1, registry.get("empioner.users.registered").counter().count());
    }
}
