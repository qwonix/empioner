package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import graphql.ExecutionResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.exception.EpisodeNotFoundException;
import ru.qwonix.empioner.telegram.exception.VideoNotFoundException;
import ru.qwonix.empioner.telegram.entity.VideoDetails;
import ru.qwonix.empioner.telegram.id.*;
import ru.qwonix.empioner.telegram.service.api.VideoApi;
import ru.qwonix.empioner.telegram.service.api.graphql.model.VideoInput;
import ru.qwonix.empioner.telegram.service.api.mapper.VideoMapper;
import ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config.coercing.EpisodeIdCoercing;
import ru.qwonix.empioner.telegram.service.model.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = EpisodeVideoGraphQLTest.App.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class EpisodeVideoGraphQLTest {
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(VideoGraphQLController.class)
    @ComponentScan(basePackageClasses = EpisodeIdCoercing.class)
    static class App {}

    @Autowired DgsQueryExecutor executor;
    @MockitoBean VideoApi api;
    @MockitoBean VideoMapper mapper;

    @Test
    void createsEpisodeVideoWithLargeMetadata() {
        var episode = new EpisodeId(UUID.randomUUID());
        var id = new VideoId(UUID.randomUUID());
        var group = new VideoGroupId(UUID.randomUUID());
        var request = new EpisodeVideoRequest("1080p", new TelegramFileId("file"),
                new TelegramFileUniqueId("unique"), 2, true,
                new VideoMetadataRequest(1920, 1080, 100, "video/mp4", 3_000_000_000L, "episode.mp4"));
        var video = new Video(id, group, "1080p", request.telegramFileId(), request.telegramFileUniqueId(), 2, true);
        when(api.addToEpisode(episode, request)).thenReturn(video);
        when(mapper.toInput(video)).thenReturn(VideoInput.newBuilder().id(id).videoGroupId(group).description("1080p")
                .telegramFileId(request.telegramFileId()).telegramFileUniqueId(request.telegramFileUniqueId())
                .priority(2).isAvailable(true).build());
        ExecutionResult result = executor.execute("""
                mutation Add($episode: EpisodeId!, $input: CreateEpisodeVideoInput!) {
                  addVideoToEpisode(episodeId: $episode, input: $input) { id videoGroupId description }
                }
                """, Map.of("episode", episode.value().toString(), "input", Map.of(
                "description", "1080p", "telegramFileId", "file", "telegramFileUniqueId", "unique", "priority", 2,
                "isAvailable", true, "details", Map.of("width", 1920, "height", 1080, "duration", 100,
                        "mimeType", "video/mp4", "fileSize", 3_000_000_000L, "fileName", "episode.mp4"))));
        assertTrue(result.getErrors().isEmpty(), result.getErrors().toString());
        verify(api).addToEpisode(episode, request);
    }

    @Test
    void updatesOnlySpecifiedFields() {
        var id = new VideoId(UUID.randomUUID());
        var group = new VideoGroupId(UUID.randomUUID());
        var request = new UpdateVideoRequest("4K", null, null, 3, null, null);
        var video = new Video(id, group, "4K", new TelegramFileId("file"), new TelegramFileUniqueId("unique"), 3, true);
        when(api.update(id, request)).thenReturn(video);
        when(mapper.toInput(video)).thenReturn(VideoInput.newBuilder().id(id).videoGroupId(group).description("4K")
                .telegramFileId(video.telegramFileId()).telegramFileUniqueId(video.telegramFileUniqueId())
                .priority(3).isAvailable(true).build());
        ExecutionResult result = executor.execute("""
                mutation Update($id: VideoId!, $input: UpdateVideoInput!) {
                  updateVideo(id: $id, input: $input) { id description priority }
                }
                """, Map.of("id", id.value().toString(), "input", Map.of("description", "4K", "priority", 3)));
        assertTrue(result.getErrors().isEmpty(), result.getErrors().toString());
        verify(api).update(id, request);
    }

    @Test
    void rejectsInvalidAndEmptyInputs() {
        var episode = UUID.randomUUID().toString();
        String add = "mutation Add($id: EpisodeId!, $input: CreateEpisodeVideoInput!) { addVideoToEpisode(episodeId: $id, input: $input) { id } }";
        var result = executor.execute(add, Map.of("id", episode, "input", Map.of("description", " ",
                "telegramFileId", "file", "telegramFileUniqueId", "unique", "priority", 1, "isAvailable", true)));
        assertFalse(result.getErrors().isEmpty());
        String update = "mutation Update($id: VideoId!, $input: UpdateVideoInput!) { updateVideo(id: $id, input: $input) { id } }";
        var noFields = executor.execute(update, Map.of("id", UUID.randomUUID().toString(), "input", Map.of()));
        assertFalse(noFields.getErrors().isEmpty());
        verifyNoInteractions(api);
    }

    @Test
    void reportsMissingEpisodeAndMissingVideo() {
        when(api.addToEpisode(any(), any())).thenThrow(new EpisodeNotFoundException());
        var add = executor.execute("""
                mutation Add($id: EpisodeId!, $input: CreateEpisodeVideoInput!) {
                  addVideoToEpisode(episodeId: $id, input: $input) { id }
                }
                """, Map.of("id", UUID.randomUUID().toString(), "input", Map.of("description", "HD",
                "telegramFileId", "file", "telegramFileUniqueId", "unique", "priority", 1, "isAvailable", true)));
        assertTrue(add.getErrors().getFirst().getMessage().contains("Episode not found"));
        when(api.update(any(), any())).thenThrow(new VideoNotFoundException());
        var update = executor.execute("""
                mutation Update($id: VideoId!, $input: UpdateVideoInput!) { updateVideo(id: $id, input: $input) { id } }
                """, Map.of("id", UUID.randomUUID().toString(), "input", Map.of("priority", 1)));
        assertTrue(update.getErrors().getFirst().getMessage().contains("Video not found"));
    }

    @Test
    void readsMetadataWith64BitFileSize() {
        var id = new VideoId(UUID.randomUUID());
        when(api.findDetails(id)).thenReturn(Optional.of(new VideoDetails(id, 1920, 1080, 100,
                "video/mp4", 3_000_000_000L, "episode.mp4")));
        var result = executor.execute("query Details($id: VideoId!) { videoDetailsById(id: $id) { fileSize } }",
                Map.of("id", id.value().toString()));
        assertTrue(result.getErrors().isEmpty(), result.getErrors().toString());
        Map<String, Object> data = result.getData();
        assertEquals(3_000_000_000L, ((Map<?, ?>) data.get("videoDetailsById")).get("fileSize"));
    }
}
