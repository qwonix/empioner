package ru.qwonix.empioner.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.service.entity.Episode;
import ru.qwonix.empioner.service.entity.id.EpisodeId;
import ru.qwonix.empioner.service.entity.id.SeasonId;
import ru.qwonix.empioner.service.entity.id.VideoGroupId;
import ru.qwonix.empioner.service.service.EpisodeService;

@Controller
@RequiredArgsConstructor
public class EpisodeGraphQLController {

    private final EpisodeService episodeService;

    @QueryMapping
    public Mono<Episode> getEpisodeById(@Argument EpisodeId id) {
        return Mono.fromCallable(() -> episodeService.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Episode> getEpisodesBySeasonId(@Argument SeasonId id, @Argument int limit, @Argument int page) {
        return Flux.defer(() -> Flux.fromIterable(episodeService.findAllBySeasonIdOrderByNumberWithLimitAndPage(id, limit, page)));
    }

    @QueryMapping
    public Mono<Integer> countEpisodesBySeasonId(@Argument SeasonId id) {
        return Mono.fromCallable(() -> episodeService.countAllBySeasonId(id));
    }

    @QueryMapping
    public Mono<Episode> getEpisodeByVideoGroupId(@Argument VideoGroupId id) {
        return Mono.fromCallable(() -> episodeService.findByVideoGroupId(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }
}
