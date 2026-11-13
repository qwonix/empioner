package ru.qwonix.empioner.telegram.bot.telegram.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.qwonix.empioner.telegram.bot.api.VideoApi;
import ru.qwonix.empioner.telegram.bot.telegram.handler.DefaultUpdateHandler;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoFileIdsReplyTest {
    @Test
    void repliesToSourceMessageWithBothIdsAndEscapesHtml() throws Exception {
        TelegramClient client = mock(TelegramClient.class);
        var service = new TelegramBotExecutionService(null, client, null);
        service.replyWithVideoFileIds(message("file<&>", "unique<&>"));

        var captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(client).execute(captor.capture());
        SendMessage reply = captor.getValue();
        assertEquals("-123", reply.getChatId());
        assertEquals(42, reply.getReplyParameters().getMessageId());
        assertEquals("html", reply.getParseMode());
        assertEquals("file_id:\n<code>file&lt;&amp;&gt;</code>\n\nfile_unique_id:\n<code>unique&lt;&amp;&gt;</code>", reply.getText());
    }

    @Test
    void incomingVideoRepliesWithIdsAndUpdatesService() throws Exception {
        TelegramClient client = mock(TelegramClient.class);
        var service = new TelegramBotExecutionService(null, client, null);
        VideoApi api = mock(VideoApi.class);
        var handler = new DefaultUpdateHandler(null, null, null, api, service);
        Message source = message("BAAC_file-id", "AgAD_unique-id");
        Update update = mock(Update.class);
        when(update.getMessage()).thenReturn(source);

        handler.onVideo(update, null);

        var captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(client).execute(captor.capture());
        assertEquals("file_id:\n<code>BAAC_file-id</code>\n\nfile_unique_id:\n<code>AgAD_unique-id</code>",
                captor.getValue().getText());
        assertEquals(source.getMessageId(), captor.getValue().getReplyParameters().getMessageId());
        verify(api).updateTelegramFileIdByTelegramFileUniqueId(
                new TelegramFileUniqueId("AgAD_unique-id"), new TelegramFileId("BAAC_file-id"));
    }

    @Test
    void repliesEvenIfServiceUpdateFails() throws Exception {
        TelegramClient client = mock(TelegramClient.class);
        var service = new TelegramBotExecutionService(null, client, null);
        VideoApi api = mock(VideoApi.class);
        doThrow(new RuntimeException("Service unavailable"))
                .when(api).updateTelegramFileIdByTelegramFileUniqueId(any(), any());
        var handler = new DefaultUpdateHandler(null, null, null, api, service);
        Update update = mock(Update.class);
        Message source = message("file", "unique");
        when(update.getMessage()).thenReturn(source);

        assertDoesNotThrow(() -> handler.onVideo(update, null));
        verify(client).execute(any(SendMessage.class));
        verify(api).updateTelegramFileIdByTelegramFileUniqueId(any(), any());
    }

    @Test
    void continuesServiceUpdateIfTelegramReplyFails() throws Exception {
        TelegramClient client = mock(TelegramClient.class);
        when(client.execute(any(SendMessage.class))).thenThrow(new TelegramApiException("Telegram unavailable"));
        var service = new TelegramBotExecutionService(null, client, null);
        VideoApi api = mock(VideoApi.class);
        var handler = new DefaultUpdateHandler(null, null, null, api, service);
        Update update = mock(Update.class);
        Message source = message("file", "unique");
        when(update.getMessage()).thenReturn(source);

        assertDoesNotThrow(() -> handler.onVideo(update, null));
        verify(api).updateTelegramFileIdByTelegramFileUniqueId(any(), any());
    }

    private Message message(String fileId, String uniqueId) {
        Video video = mock(Video.class);
        when(video.getFileId()).thenReturn(fileId);
        when(video.getFileUniqueId()).thenReturn(uniqueId);
        Message message = mock(Message.class);
        when(message.getVideo()).thenReturn(video);
        when(message.getChatId()).thenReturn(-123L);
        when(message.getMessageId()).thenReturn(42);
        return message;
    }
}
