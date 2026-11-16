package ru.qwonix.empioner.telegram.bot.telegram.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.qwonix.empioner.telegram.bot.api.*;
import ru.qwonix.empioner.telegram.entity.*;
import ru.qwonix.empioner.telegram.id.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EpisodeVideoReplacementServiceTest {
    final ShowApi shows = mock(ShowApi.class);
    final SeriesApi series = mock(SeriesApi.class);
    final SeasonApi seasons = mock(SeasonApi.class);
    final EpisodeApi episodes = mock(EpisodeApi.class);
    final VideoApi videos = mock(VideoApi.class);
    final EpisodeVideoReplacementService service = new EpisodeVideoReplacementService(shows, series, seasons, episodes, videos);
    final TelegramBotUser admin = new TelegramBotUser(new TelegramBotUserId(123L), Set.of(UserRole.ADMIN));

    @ParameterizedTest
    @CsvSource({
            "1080,FullHD,Full HD,Some video 704 1080p x264 final.mp4",
            "1080,1080p,Full HD,704 603.mp4",
            "480,SD,SD,704 480P.mp4",
            "480,480p,SD,480P episode 704 x264.mp4",
            "480,SD,SD,480 P episode 704.mp4"
    })
    void findsEpisodeAndReplacesBothIdsUsingQualityAndMetadata(int height, String existingQuality, String expectedQuality, String filename) {
        Message message = input(filename, height);
        var show = mock(Show.class);
        var seriesItem = mock(Series.class);
        var season = mock(Season.class);
        var episode = mock(Episode.class);
        var video = mock(Video.class);
        var showId = new ShowId(UUID.randomUUID());
        var seriesId = new SeriesId(UUID.randomUUID());
        var seasonId = new SeasonId(UUID.randomUUID());
        var groupId = new VideoGroupId(UUID.randomUUID());
        var videoId = new VideoId(UUID.randomUUID());
        when(show.id()).thenReturn(showId);
        when(seriesItem.id()).thenReturn(seriesId);
        when(season.id()).thenReturn(seasonId);
        when(season.number()).thenReturn(7);
        when(episode.number()).thenReturn(4);
        when(episode.videoGroupId()).thenReturn(groupId);
        when(video.id()).thenReturn(videoId);
        when(video.description()).thenReturn(existingQuality);
        when(shows.findAllOrderByNumberWithLimitAndPage(100, 0)).thenReturn(List.of(show));
        when(series.findAllByShowId(showId)).thenReturn(List.of(seriesItem));
        when(seasons.countAllBySeries(seriesId)).thenReturn(1);
        when(seasons.findAllBySeriesIdOrderByNumberWithLimitAndPage(seriesId, 100, 0)).thenReturn(List.of(season));
        when(episodes.countAllBySeasonId(seasonId)).thenReturn(1);
        when(episodes.findAllBySeasonIdOrderByNumberWithLimitAndPage(seasonId, 100, 0)).thenReturn(List.of(episode));
        when(videos.findAllByVideoGroupId(groupId)).thenReturn(List.of(video));

        assertEquals("Обновлено: сезон 7, серия 4, " + expectedQuality + ".", service.replace(message, admin));
        verify(videos).replaceVideo(videoId, expectedQuality, message.getVideo());
        verify(videos, never()).addVideoToEpisode(any(), any(), anyInt(), any());
    }

    @ParameterizedTest
    @CsvSource({"480,FullHD,1,SD,2", "1080,SD,2,Full HD,1", "480,4K,10,SD,11"})
    void addsMissingQualityWithPriorityBelowOrAboveExisting(int height, String existingQuality,
                                                             int existingPriority, String expectedQuality, int expectedPriority) {
        Message message = input("704.mp4", height);
        var show = mock(Show.class);
        var seriesItem = mock(Series.class);
        var season = mock(Season.class);
        var episode = mock(Episode.class);
        var existing = mock(Video.class);
        var showId = new ShowId(UUID.randomUUID());
        var seriesId = new SeriesId(UUID.randomUUID());
        var seasonId = new SeasonId(UUID.randomUUID());
        var episodeId = new EpisodeId(UUID.randomUUID());
        var groupId = new VideoGroupId(UUID.randomUUID());
        when(show.id()).thenReturn(showId);
        when(seriesItem.id()).thenReturn(seriesId);
        when(season.id()).thenReturn(seasonId);
        when(season.number()).thenReturn(7);
        when(episode.id()).thenReturn(episodeId);
        when(episode.number()).thenReturn(4);
        when(episode.videoGroupId()).thenReturn(groupId);
        when(existing.description()).thenReturn(existingQuality);
        when(existing.priority()).thenReturn(existingPriority);
        when(existing.isAvailable()).thenReturn(true);
        when(shows.findAllOrderByNumberWithLimitAndPage(100, 0)).thenReturn(List.of(show));
        when(series.findAllByShowId(showId)).thenReturn(List.of(seriesItem));
        when(seasons.countAllBySeries(seriesId)).thenReturn(1);
        when(seasons.findAllBySeriesIdOrderByNumberWithLimitAndPage(seriesId, 100, 0)).thenReturn(List.of(season));
        when(episodes.countAllBySeasonId(seasonId)).thenReturn(1);
        when(episodes.findAllBySeasonIdOrderByNumberWithLimitAndPage(seasonId, 100, 0)).thenReturn(List.of(episode));
        when(videos.findAllByVideoGroupId(groupId)).thenReturn(List.of(existing));

        assertEquals("Добавлено: сезон 7, серия 4, " + expectedQuality + ".", service.replace(message, admin));
        verify(videos).addVideoToEpisode(episodeId, expectedQuality, expectedPriority, message.getVideo());
        verify(videos, never()).replaceVideo(any(), any(), any());
    }

    @Test
    void refusesMissingFirstEpisodeAndUnsupportedResolution() {
        assertTrue(service.replace(input("999 704.mp4", 1080), admin).contains("по коду 999"));
        assertTrue(service.replace(input("704.mp4", 720), admin).contains("1080 и 480"));
        assertTrue(service.replace(input("1704.mp4", 1080), admin).contains("не найден"));
        assertTrue(service.replace(input("704 480p.mp4", 480), admin).contains("найдено 0"));
        verifyNoInteractions(videos);
    }

    @Test
    void ignoresNonAdminsAndGroups() {
        Message message = input("704.mp4", 480);
        var user = new TelegramBotUser(new TelegramBotUserId(123L));
        assertEquals("", service.replace(message, user));
        when(message.getChatId()).thenReturn(-123L);
        assertEquals("", service.replace(message, admin));
        verifyNoInteractions(shows, videos);
    }

    @Test
    void mapsOldQualityNames() {
        assertEquals("SD", EpisodeVideoReplacementService.normalizeQuality("480p"));
        assertEquals("SD", EpisodeVideoReplacementService.normalizeQuality("SD"));
        assertEquals("Full HD", EpisodeVideoReplacementService.normalizeQuality("1080p"));
        assertEquals("Full HD", EpisodeVideoReplacementService.normalizeQuality("FullHD"));
    }

    private Message input(String name, int height) {
        var video = mock(org.telegram.telegrambots.meta.api.objects.Video.class);
        when(video.getFileName()).thenReturn(name);
        when(video.getHeight()).thenReturn(height);
        when(video.getWidth()).thenReturn(1920);
        when(video.getDuration()).thenReturn(100);
        when(video.getMimeType()).thenReturn("video/mp4");
        when(video.getFileSize()).thenReturn(1000L);
        when(video.getFileId()).thenReturn("new-id");
        when(video.getFileUniqueId()).thenReturn("new-unique-id");
        var message = mock(Message.class);
        when(message.getVideo()).thenReturn(video);
        when(message.getChatId()).thenReturn(123L);
        return message;
    }
}
