package ru.qwonix.empioner.telegram.bot.config;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObservedTelegramClientTest {
    @Test
    void preservesFailureAndRecordsItEvenWhenCallerRecovers() throws Exception {
        TelegramClient delegate = mock(TelegramClient.class);
        TelegramApiException failure = new TelegramApiException("unavailable");
        when(delegate.execute(any(SendMessage.class))).thenThrow(failure);
        var registry = new SimpleMeterRegistry();
        TelegramClient client = ObservedTelegramClient.wrap(delegate, registry);
        UpdateProcessingContext.begin();
        try {
            assertSame(failure, assertThrows(TelegramApiException.class,
                    () -> client.execute(SendMessage.builder().chatId(123L).text("test").build())));
            assertTrue(UpdateProcessingContext.hasFailure());
            assertEquals(1, registry.get("empioner.telegram.api.requests").tag("outcome", "error").counter().count());
        } finally { UpdateProcessingContext.clear(); }
    }
}
