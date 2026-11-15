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
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.UserRole;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoFileIdsReplyTest {
    private final TelegramBotUser admin = new TelegramBotUser(new TelegramBotUserId(123L), Set.of(UserRole.ADMIN));
    private final TelegramBotUser user = new TelegramBotUser(new TelegramBotUserId(123L));

    @Test
    void repliesToSourceMessageWithBothIdsAndEscapesHtml() throws Exception {
        TelegramClient client = mock(TelegramClient.class);
        var service = new TelegramBotExecutionService(null, client, null);
        Message source = message("file<&>", "unique<&>");
        when(source.getVideo().getFileName()).thenReturn("video<&>.mp4");
        service.replyWithVideoFileIds(source, admin);

        var captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(client).execute(captor.capture());
        SendMessage reply = captor.getValue();
        assertEquals("123", reply.getChatId());
        assertEquals(42, reply.getReplyParameters().getMessageId());
        assertEquals("html", reply.getParseMode());
        assertEquals("file_name:\n<code>video&lt;&amp;&gt;.mp4</code>\n\nfile_id:\n<code>file&lt;&amp;&gt;</code>\n\nfile_unique_id:\n<code>unique&lt;&amp;&gt;</code>", reply.getText());
    }

    @Test
    void incomingVideoRepliesWithIdsAndUpdatesService() throws Exception {
        TelegramClient client = mock(TelegramClient.class);
        var service = new TelegramBotExecutionService(null, client, null);
        VideoApi api = mock(VideoApi.class);
        var handler = new DefaultUpdateHandler(null, null, null, api, service, noReplacement());
        Message source = message("BAAC_file-id", "AgAD_unique-id");
        Update update = mock(Update.class);
        when(update.getMessage()).thenReturn(source);

        handler.onVideo(update, admin);

        var captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(client).execute(captor.capture());
        assertEquals("file_name:\n<code>не указано</code>\n\nfile_id:\n<code>BAAC_file-id</code>\n\nfile_unique_id:\n<code>AgAD_unique-id</code>",
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
        var handler = new DefaultUpdateHandler(null, null, null, api, service, noReplacement());
        Update update = mock(Update.class);
        Message source = message("file", "unique");
        when(update.getMessage()).thenReturn(source);

        assertDoesNotThrow(() -> handler.onVideo(update, admin));
        verify(client).execute(any(SendMessage.class));
        verify(api).updateTelegramFileIdByTelegramFileUniqueId(any(), any());
    }

    @Test
    void continuesServiceUpdateIfTelegramReplyFails() throws Exception {
        TelegramClient client = mock(TelegramClient.class);
        when(client.execute(any(SendMessage.class))).thenThrow(new TelegramApiException("Telegram unavailable"));
        var service = new TelegramBotExecutionService(null, client, null);
        VideoApi api = mock(VideoApi.class);
        var handler = new DefaultUpdateHandler(null, null, null, api, service, noReplacement());
        Update update = mock(Update.class);
        Message source = message("file", "unique");
        when(update.getMessage()).thenReturn(source);

        assertDoesNotThrow(() -> handler.onVideo(update, admin));
        verify(api).updateTelegramFileIdByTelegramFileUniqueId(any(), any());
    }

    @Test
    void doesNotReplyToNonAdminButStillUpdatesService() throws Exception {
        TelegramClient client = mock(TelegramClient.class);
        VideoApi api = mock(VideoApi.class);
        var handler = new DefaultUpdateHandler(null, null, null, api,
                new TelegramBotExecutionService(null, client, null), noReplacement());
        Update update = mock(Update.class);
        Message source = message("file", "unique");
        when(update.getMessage()).thenReturn(source);
        handler.onVideo(update, user);
        verify(client, never()).execute(any(SendMessage.class));
        verify(api).updateTelegramFileIdByTelegramFileUniqueId(any(), any());
    }

    @Test
    void doesNotDiscloseIdentifiersToGroupEvenForAdmin() throws Exception {
        TelegramClient client = mock(TelegramClient.class);
        var service = new TelegramBotExecutionService(null, client, null);
        Message groupMessage = message("file", "unique");
        when(groupMessage.getChatId()).thenReturn(-123L);
        service.replyWithVideoFileIds(groupMessage, admin);
        verify(client, never()).execute(any(SendMessage.class));
    }

    private EpisodeVideoReplacementService noReplacement() {
        var service = mock(EpisodeVideoReplacementService.class);
        when(service.replace(any(), any())).thenReturn("");
        return service;
    }

    private Message message(String fileId, String uniqueId) {
        Video video = mock(Video.class);
        when(video.getFileId()).thenReturn(fileId);
        when(video.getFileUniqueId()).thenReturn(uniqueId);
        Message message = mock(Message.class);
        when(message.getVideo()).thenReturn(video);
        when(message.getChatId()).thenReturn(123L);
        when(message.getMessageId()).thenReturn(42);
        return message;
    }
}
