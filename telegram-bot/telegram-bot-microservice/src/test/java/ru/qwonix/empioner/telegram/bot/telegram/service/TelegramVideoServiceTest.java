package ru.qwonix.empioner.telegram.bot.telegram.service;

import org.junit.jupiter.api.Test;
import ru.qwonix.empioner.telegram.bot.api.VideoApi;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelegramVideoServiceTest {
    @Test
    void hidesUnavailableVideosIncludingOldCallbacks() {
        VideoApi api = mock(VideoApi.class);
        var service = new TelegramVideoService(api, null);
        var group = new VideoGroupId(UUID.randomUUID());
        var available = new Video(new VideoId(UUID.randomUUID()), group, "HD", null, null, 1, true);
        var hidden = new Video(new VideoId(UUID.randomUUID()), group, "4K", null, null, 2, false);
        when(api.findAllByVideoGroupId(group)).thenReturn(List.of(available, hidden));
        when(api.findById(hidden.id())).thenReturn(Optional.of(hidden));
        assertEquals(List.of(available), service.findAllVideoInGroup(group));
        assertTrue(service.findById(hidden.id()).isEmpty());
    }
    @Test
    void alternativesExcludeSelectedAndUnavailableVideos() {
        VideoApi api = mock(VideoApi.class);
        var service = new TelegramVideoService(api, null);
        var group = new VideoGroupId(UUID.randomUUID());
        var selected = new Video(new VideoId(UUID.randomUUID()), group, "HD", null, null, 10, true);
        var other = new Video(new VideoId(UUID.randomUUID()), group, "SD", null, null, 5, true);
        var hidden = new Video(new VideoId(UUID.randomUUID()), group, "4K", null, null, 20, false);
        when(api.findAllByVideoGroupId(group)).thenReturn(List.of(selected, other, hidden));
        assertEquals(List.of(other), service.findOtherVideos(selected));
        verify(api).findAllByVideoGroupId(group);
    }
}
