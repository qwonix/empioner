package ru.qwonix.empioner.telegram.bot.telegram.callback.handler;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.qwonix.empioner.telegram.bot.api.MessageApi;
import ru.qwonix.empioner.telegram.bot.api.VideoApi;
import ru.qwonix.empioner.telegram.bot.telegram.callback.data.EpisodeCallbackData;
import ru.qwonix.empioner.telegram.bot.telegram.config.TelegramProperties;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramBotExecutionService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramEpisodeService;
import ru.qwonix.empioner.telegram.bot.telegram.service.TelegramVideoService;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EpisodeCallbackDataHandlerTest {
    @Test
    void selectingEpisodeSendsVideoAndKeepsAvailableAlternatives() throws Exception {
        var episodes = mock(TelegramEpisodeService.class);
        var api = mock(VideoApi.class);
        var properties = mock(TelegramProperties.class);
        when(properties.username()).thenReturn("test_bot");
        var videos = new TelegramVideoService(api, properties);
        var execution = mock(TelegramBotExecutionService.class);
        var messages = mock(MessageApi.class);
        var telegram = mock(TelegramClient.class);
        var handler = new EpisodeCallbackDataHandler(episodes, videos, execution, messages, telegram);

        var group = new VideoGroupId(UUID.randomUUID());
        var episodeId = new EpisodeId(UUID.randomUUID());
        var episode = new Episode(episodeId, "Episode", "", 1, null, null, group, null, true);
        var selected = new Video(new VideoId(UUID.randomUUID()), group, "HD", new TelegramFileId("file1"), null, 10, true);
        var other = new Video(new VideoId(UUID.randomUUID()), group, "SD", new TelegramFileId("file2"), null, 5, true);
        var hidden = new Video(new VideoId(UUID.randomUUID()), group, "Hidden", new TelegramFileId("file3"), null, 1, false);
        var user = new TelegramBotUser(new TelegramBotUserId(42L));
        var sent = mock(Message.class);
        when(sent.getMessageId()).thenReturn(7);
        when(episodes.findById(episodeId)).thenReturn(Optional.of(episode));
        when(episodes.createText(episode)).thenReturn("Episode");
        when(api.findMaxPriorityInGroup(group)).thenReturn(Optional.of(selected));
        when(api.findAllByVideoGroupId(group)).thenReturn(List.of(selected, other, hidden));
        when(telegram.execute(any(SendVideo.class))).thenReturn(sent);

        handler.handle(user, mock(CallbackQuery.class), new EpisodeCallbackData(episodeId));

        verify(episodes).createKeyboard(episode, List.of(other));
        verify(telegram).execute(any(SendVideo.class));
        verify(messages).setMessageId(user, 7);
        verifyNoInteractions(execution);
    }
}
