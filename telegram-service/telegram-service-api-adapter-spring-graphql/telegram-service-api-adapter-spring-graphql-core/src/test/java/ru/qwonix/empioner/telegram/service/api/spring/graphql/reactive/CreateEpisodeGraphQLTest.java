package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import graphql.ExecutionResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.api.EpisodeApi;
import ru.qwonix.empioner.telegram.service.api.graphql.model.EpisodeInput;
import ru.qwonix.empioner.telegram.service.api.mapper.EpisodeMapper;
import ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config.coercing.EpisodeIdCoercing;
import ru.qwonix.empioner.telegram.service.model.CreateEpisodeRequest;

import java.util.Map;
import ru.qwonix.empioner.telegram.exception.SeasonNotFoundException;
import ru.qwonix.empioner.telegram.exception.EpisodeAlreadyExistsException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CreateEpisodeGraphQLTest.App.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CreateEpisodeGraphQLTest {
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(EpisodeGraphQLController.class)
    @ComponentScan(basePackageClasses = EpisodeIdCoercing.class)
    static class App {}

    @Autowired DgsQueryExecutor executor;
    @MockitoBean EpisodeApi episodeApi;
    @MockitoBean EpisodeMapper mapper;

    @Test
    void createsEpisodeViaGraphQLVariables() {
        UUID season = UUID.randomUUID();
        EpisodeId id = new EpisodeId(UUID.randomUUID());
        VideoGroupId group = new VideoGroupId(UUID.randomUUID());
        var request = new CreateEpisodeRequest(new SeasonId(season), "Pilot", "Description", 1);
        var episode = new Episode(id, "Pilot", "Description", 1, null, null, group, new SeasonId(season), null, false);
        when(episodeApi.create(request)).thenReturn(episode);
        when(mapper.toInput(episode)).thenReturn(EpisodeInput.newBuilder()
                .id(id).title("Pilot").description("Description").number(1)
                .seasonId(new SeasonId(season)).videoGroupId(group).isAvailable(false).build());

        ExecutionResult result = executor.execute("""
                mutation Create($input: CreateEpisodeInput!) {
                  createEpisode(input: $input) { id seasonId title number videoGroupId isAvailable }
                }
                """, Map.of("input", Map.of("seasonId", season.toString(), "title", "Pilot",
                "description", "Description", "number", 1)));
        assertTrue(result.getErrors().isEmpty(), result.getErrors().toString());
        Map<String, Object> data = result.getData();
        Map<?, ?> created = (Map<?, ?>) data.get("createEpisode");
        assertEquals(id.value().toString(), created.get("id"));
        assertEquals(season.toString(), created.get("seasonId"));
        assertEquals(false, created.get("isAvailable"));
        verify(episodeApi).create(request);
    }

    @Test
    void rejectsInvalidNumberAndReportsValidationError() {
        ExecutionResult result = executor.execute("""
                mutation Create($input: CreateEpisodeInput!) { createEpisode(input: $input) { id } }
                """, Map.of("input", Map.of("seasonId", UUID.randomUUID().toString(), "title", "Pilot",
                "description", "Description", "number", 0)));
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().getFirst().getMessage().contains("Episode number must be positive"));
        verifyNoInteractions(episodeApi);
    }

    @Test
    void reportsMissingSeasonAndDuplicateNumber() {
        var input = Map.of("seasonId", UUID.randomUUID().toString(), "title", "Pilot",
                "description", "Description", "number", 1);
        String mutation = "mutation Create($input: CreateEpisodeInput!) { createEpisode(input: $input) { id } }";
        when(episodeApi.create(any()))
                .thenThrow(new SeasonNotFoundException())
                .thenThrow(new EpisodeAlreadyExistsException());
        var missing = executor.execute(mutation, Map.of("input", input));
        var duplicate = executor.execute(mutation, Map.of("input", input));
        assertTrue(missing.getErrors().getFirst().getMessage().contains("Season not found"));
        assertTrue(duplicate.getErrors().getFirst().getMessage().contains("Episode number already exists in this season"));
    }

    @Test
    void missingRequiredFieldFailsBeforeServiceCall() {
        ExecutionResult result = executor.execute("""
                mutation Create($input: CreateEpisodeInput!) { createEpisode(input: $input) { id } }
                """, Map.of("input", Map.of("seasonId", UUID.randomUUID().toString(), "title", "Pilot", "number", 1)));
        assertFalse(result.getErrors().isEmpty());
        verifyNoInteractions(episodeApi);
    }
}
