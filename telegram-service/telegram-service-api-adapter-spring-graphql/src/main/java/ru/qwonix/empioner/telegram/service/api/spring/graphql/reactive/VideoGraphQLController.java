package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;
import ru.qwonix.empioner.telegram.service.api.VideoApi;
import ru.qwonix.empioner.telegram.service.api.graphql.api.VideoQueryResolver;
import ru.qwonix.empioner.telegram.service.api.graphql.model.VideoInput;
import ru.qwonix.empioner.telegram.service.api.mapper.VideoMapper;

@Controller
@RequiredArgsConstructor
public class VideoGraphQLController implements VideoQueryResolver {

    private final VideoApi videoApi;
    private final VideoMapper mapper;

    @QueryMapping
    public Mono<VideoInput> getMaxPriorityVideo(@Argument VideoGroupId id) {
        return Mono.fromCallable(() -> videoApi.findMaxPriorityInGroup(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Mono<VideoInput> getVideoById(@Argument VideoId id) {
        return Mono.fromCallable(() -> videoApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<VideoInput> getVideosByGroup(@Argument VideoGroupId id) {
        return Flux.defer(() -> Flux.fromIterable(videoApi.findAllByVideoGroupId(id)))
                .map(mapper::toInput);
    }
}
