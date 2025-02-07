package ru.qwonix.empioner.telegram.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.service.entity.Video;
import ru.qwonix.empioner.telegram.service.entity.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.entity.id.VideoId;
import ru.qwonix.empioner.telegram.service.service.VideoService;

@Controller
@RequiredArgsConstructor
public class VideoGraphQLController {

    private final VideoService videoService;

    @QueryMapping
    public Mono<Video> getMaxPriorityVideo(@Argument VideoGroupId id) {
        return Mono.fromCallable(() -> videoService.findMaxPriorityInGroup(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Mono<Video> getVideoById(@Argument VideoId id) {
        return Mono.fromCallable(() -> videoService.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Video> getVideosByGroup(@Argument VideoGroupId id) {
        return Flux.defer(() -> Flux.fromIterable(videoService.findAllByVideoGroupId(id)));
    }
}
