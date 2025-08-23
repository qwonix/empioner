package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;
import ru.qwonix.empioner.telegram.service.api.VideoApi;
import ru.qwonix.empioner.telegram.service.api.graphql.model.AddVideoInput;
import ru.qwonix.empioner.telegram.service.api.graphql.model.VideoInput;
import ru.qwonix.empioner.telegram.service.api.mapper.VideoMapper;

@DgsComponent
@RequiredArgsConstructor
public class VideoGraphQLController {

    private final VideoApi videoApi;
    private final VideoMapper mapper;

    @DgsQuery
    public Mono<VideoInput> maxPriorityVideo(@InputArgument VideoGroupId id) {
        return Mono.fromCallable(() -> videoApi.findMaxPriorityInGroup(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @DgsQuery
    public Mono<VideoInput> videoById(@InputArgument VideoId id) {
        return Mono.fromCallable(() -> videoApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @DgsQuery
    public Flux<VideoInput> videosByVideoGroupId(@InputArgument VideoGroupId id) {
        return Flux.defer(() -> Flux.fromIterable(videoApi.findAllByVideoGroupId(id)))
                .map(mapper::toInput);
    }

    @DgsMutation
    public Mono<VideoId> create(@InputArgument AddVideoInput video, @InputArgument Boolean needCreateNewVideoGroup) {
        return Mono.fromCallable(() -> videoApi.create(mapper.toRequest(video), needCreateNewVideoGroup));
    }

    @DgsMutation
    public Mono<Boolean> updateTelegramFileId(
            @InputArgument TelegramFileUniqueId id,
            @InputArgument TelegramFileId newTelegramFileId) {
        return Mono.fromCallable(() -> videoApi.updateTelegramFileIdByTelegramFileUniqueId(
                id,
                newTelegramFileId
        ));
    }
}
