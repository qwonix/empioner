package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.api.EpisodeApi;
import ru.qwonix.empioner.telegram.service.api.graphql.api.EpisodeMutationResolver;
import ru.qwonix.empioner.telegram.service.api.graphql.api.EpisodeQueryResolver;
import ru.qwonix.empioner.telegram.service.api.graphql.model.EpisodeInput;
import ru.qwonix.empioner.telegram.service.api.mapper.EpisodeMapper;

@Controller
@RequiredArgsConstructor
public class EpisodeGraphQLController implements EpisodeQueryResolver, EpisodeMutationResolver {

    private final EpisodeApi episodeApi;
    private final EpisodeMapper mapper;

    @QueryMapping
    @Override
    public Mono<EpisodeInput> getEpisodeById(@Argument EpisodeId id) {
        return Mono.fromCallable(() -> episodeApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    @Override
    public Flux<EpisodeInput> getEpisodesBySeasonId(@Argument SeasonId id, @Argument Integer limit, @Argument Integer page) {
        return Flux.defer(() -> Flux.fromIterable(episodeApi.findAllBySeasonIdOrderByNumberWithLimitAndPage(id, limit, page)))
                .map(mapper::toInput);
    }

    @QueryMapping
    @Override
    public Mono<Integer> countEpisodesBySeasonId(@Argument SeasonId id) {
        return Mono.fromCallable(() -> episodeApi.countAllBySeasonId(id));
    }

    @QueryMapping
    @Override
    public Mono<EpisodeInput> getEpisodeByVideoGroupId(@Argument VideoGroupId id) {
        return Mono.fromCallable(() -> episodeApi.findByVideoGroupId(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @MutationMapping
    @Override
    public Mono<Boolean> changeAvailable(@Argument EpisodeId id, @Argument boolean isAvailable) {
        return Mono.fromCallable(() -> episodeApi.changeAvailable(id, isAvailable));
    }
}
