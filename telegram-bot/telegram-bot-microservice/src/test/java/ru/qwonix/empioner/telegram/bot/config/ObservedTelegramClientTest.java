package ru.qwonix.empioner.telegram.bot.config;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObservedTelegramClientTest {
    @Test
    void countsOnlyVideoDeliveriesAndPreservesTelegramFailures() throws Exception {
        TelegramClient delegate = mock(TelegramClient.class);
        var registry = new SimpleMeterRegistry();
        TelegramClient client = ObservedTelegramClient.wrap(delegate, registry);

        assertEquals(0, registry.get("empioner.video.delivery").tag("operation", "send")
                .tag("outcome", "error").counter().count());
        var send = SendVideo.builder().chatId(123L).video(new InputFile("video-file")).build();
        var videoEdit = EditMessageMedia.builder().chatId(123L).messageId(1)
                .media(InputMediaVideo.builder().media("video-file").build()).build();
        var photoEdit = EditMessageMedia.builder().chatId(123L).messageId(1)
                .media(InputMediaPhoto.builder().media("photo-file").build()).build();
        client.execute(send);
        client.execute(videoEdit);
        client.execute(photoEdit);
        TelegramApiException failure = new TelegramApiException("unavailable");
        when(delegate.execute(any(SendVideo.class))).thenThrow(failure);
        assertSame(failure, assertThrows(TelegramApiException.class, () -> client.execute(send)));

        assertEquals(1, registry.get("empioner.video.delivery").tag("operation", "send")
                .tag("outcome", "success").counter().count());
        assertEquals(1, registry.get("empioner.video.delivery").tag("operation", "send")
                .tag("outcome", "error").counter().count());
        assertEquals(1, registry.get("empioner.video.delivery").tag("operation", "edit")
                .tag("outcome", "success").counter().count());
    }

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
