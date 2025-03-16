package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;
import ru.qwonix.empioner.telegram.service.api.VideoApi;

@Controller
@RequiredArgsConstructor
public class VideoGraphQLController {

    private final VideoApi videoApi;

    @QueryMapping
    public Mono<Video> getMaxPriorityVideo(@Argument VideoGroupId id) {
        return Mono.fromCallable(() -> videoApi.findMaxPriorityInGroup(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Mono<Video> getVideoById(@Argument VideoId id) {
        return Mono.fromCallable(() -> videoApi.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Video> getVideosByGroup(@Argument VideoGroupId id) {
        return Flux.defer(() -> Flux.fromIterable(videoApi.findAllByVideoGroupId(id)));
    }
}
