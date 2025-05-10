package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.api.EpisodeApi;
import ru.qwonix.empioner.telegram.service.api.graphql.model.EpisodeInput;
import ru.qwonix.empioner.telegram.service.api.mapper.EpisodeMapper;

@DgsComponent
@RequiredArgsConstructor
public class EpisodeGraphQLController {

    private final EpisodeApi episodeApi;
    private final EpisodeMapper mapper;

    @DgsQuery
    public Mono<EpisodeInput> getEpisodeById(@InputArgument EpisodeId id) {
        return Mono.fromCallable(() -> episodeApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @DgsQuery
    public Flux<EpisodeInput> getEpisodesBySeasonId(@InputArgument SeasonId id, @InputArgument Integer limit, @InputArgument Integer page) {
        return Flux.defer(() -> Flux.fromIterable(episodeApi.findAllBySeasonIdOrderByNumberWithLimitAndPage(id, limit, page)))
                .map(mapper::toInput);
    }

    @DgsQuery
    public Mono<Integer> countEpisodesBySeasonId(@InputArgument SeasonId id) {
        return Mono.fromCallable(() -> episodeApi.countAllBySeasonId(id));
    }

    @DgsQuery
    public Mono<EpisodeInput> getEpisodeByVideoGroupId(@InputArgument VideoGroupId id) {
        return Mono.fromCallable(() -> episodeApi.findByVideoGroupId(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @DgsMutation
    public Mono<Boolean> changeAvailable(@InputArgument EpisodeId id, @InputArgument Boolean isAvailable) {
        return Mono.fromCallable(() -> episodeApi.changeAvailable(id, isAvailable));
    }
}
