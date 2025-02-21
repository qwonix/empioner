package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.api.EpisodeApi;

@Controller
@RequiredArgsConstructor
public class EpisodeGraphQLController {

    private final EpisodeApi episodeApi;

    @QueryMapping
    public Mono<Episode> getEpisodeById(@Argument EpisodeId id) {
        return Mono.fromCallable(() -> episodeApi.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Episode> getEpisodesBySeasonId(@Argument SeasonId id, @Argument int limit, @Argument int page) {
        return Flux.defer(() -> Flux.fromIterable(episodeApi.findAllBySeasonIdOrderByNumberWithLimitAndPage(id, limit, page)));
    }

    @QueryMapping
    public Mono<Integer> countEpisodesBySeasonId(@Argument SeasonId id) {
        return Mono.fromCallable(() -> episodeApi.countAllBySeasonId(id));
    }

    @QueryMapping
    public Mono<Episode> getEpisodeByVideoGroupId(@Argument VideoGroupId id) {
        return Mono.fromCallable(() -> episodeApi.findByVideoGroupId(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @MutationMapping
    public Mono<Boolean> changeAvailable(@Argument EpisodeId id, @Argument Boolean isAvailable) {
        return Mono.fromCallable(() -> episodeApi.changeAvailable(id, isAvailable));
    }
}
