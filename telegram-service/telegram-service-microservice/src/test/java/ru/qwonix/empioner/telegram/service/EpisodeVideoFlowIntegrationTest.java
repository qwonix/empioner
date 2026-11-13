package ru.qwonix.empioner.telegram.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EpisodeVideoFlowIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    // Episode and video operations use PostgreSQL; never contact a real MongoDB.
    @MockitoBean MongoTemplate mongoTemplate;
    @LocalServerPort int port;
    @Autowired JdbcClient jdbc;
    @Autowired ObjectMapper json;
    final HttpClient http = HttpClient.newHttpClient();

    @Test
    void createsEpisodeAndVideoPublishesAndReadsThem() throws Exception {
        UUID show = UUID.randomUUID(), series = UUID.randomUUID(), season = UUID.randomUUID();
        jdbc.sql("insert into show (id,title,priority,is_available) values (:id,'Test',1,true)")
                .param("id", show).update();
        jdbc.sql("insert into series (id,title,show_id,priority,is_available) values (:id,'Test',:show,1,true)")
                .param("id", series).param("show", show).update();
        jdbc.sql("insert into season (id,number,series_id,is_available) values (:id,1,:series,true)")
                .param("id", season).param("series", series).update();

        JsonNode created = graphql("""
                mutation($input: CreateEpisodeInput!) {
                  createEpisode(input: $input) { id videoGroupId isAvailable }
                }
                """, Map.of("input", Map.of("seasonId", season.toString(), "title", "Episode",
                "description", "", "number", 1))).path("createEpisode");
        String episode = created.path("id").asText();
        String group = created.path("videoGroupId").asText();
        assertFalse(episode.isBlank());
        assertFalse(group.isBlank());
        assertFalse(created.path("isAvailable").asBoolean());

        JsonNode video = graphql("""
                mutation($episode: EpisodeId!, $input: CreateEpisodeVideoInput!) {
                  addVideoToEpisode(episodeId: $episode, input: $input) {
                    id videoGroupId isAvailable priority
                  }
                }
                """, Map.of("episode", episode, "input", Map.of(
                "description", "HD", "telegramFileId", "test-file-id",
                "telegramFileUniqueId", "test-unique-id", "priority", 1,
                "isAvailable", false, "details", Map.of(
                        "width", 1920, "height", 1080, "duration", 120,
                        "mimeType", "video/mp4", "fileSize", 3000000000L, "fileName", "test.mp4"))))
                .path("addVideoToEpisode");
        String videoId = video.path("id").asText();
        assertFalse(videoId.isBlank());
        assertEquals(group, video.path("videoGroupId").asText());
        assertFalse(video.path("isAvailable").asBoolean());

        assertTrue(graphql("query($id: VideoGroupId!) { maxPriorityVideo(id: $id) { id } }",
                Map.of("id", group)).path("maxPriorityVideo").isNull());
        assertTrue(graphql("query($id: EpisodeId!) { episodeById(id: $id) { id } }",
                Map.of("id", episode)).path("episodeById").isNull());

        JsonNode updated = graphql("""
                mutation($id: VideoId!, $input: UpdateVideoInput!) {
                  updateVideo(id: $id, input: $input) { id isAvailable }
                }
                """, Map.of("id", videoId, "input", Map.of("isAvailable", true))).path("updateVideo");
        assertTrue(updated.path("isAvailable").asBoolean());
        assertTrue(graphql("mutation($id: EpisodeId!) { publishEpisode(id: $id) }",
                Map.of("id", episode)).path("publishEpisode").asBoolean());
        assertEquals(episode, graphql("query($id: EpisodeId!) { episodeById(id: $id) { id } }",
                Map.of("id", episode)).path("episodeById").path("id").asText());
        assertEquals(videoId, graphql("query($id: VideoGroupId!) { maxPriorityVideo(id: $id) { id } }",
                Map.of("id", group)).path("maxPriorityVideo").path("id").asText());
        assertEquals(3000000000L, graphql("query($id: VideoId!) { videoDetailsById(id: $id) { fileSize } }",
                Map.of("id", videoId)).path("videoDetailsById").path("fileSize").asLong());
        assertTrue(graphql("mutation($id: EpisodeId!) { unpublishEpisode(id: $id) }",
                Map.of("id", episode)).path("unpublishEpisode").asBoolean());
        assertTrue(graphql("query($id: EpisodeId!) { episodeById(id: $id) { id } }",
                Map.of("id", episode)).path("episodeById").isNull());
    }

    private JsonNode graphql(String query, Map<String, ?> variables) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/graphql"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(
                        Map.of("query", query, "variables", variables))))
                .build();
        var response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JsonNode body = json.readTree(response.body());
        assertFalse(body.hasNonNull("errors"), body.toString());
        return body.path("data");
    }
}
