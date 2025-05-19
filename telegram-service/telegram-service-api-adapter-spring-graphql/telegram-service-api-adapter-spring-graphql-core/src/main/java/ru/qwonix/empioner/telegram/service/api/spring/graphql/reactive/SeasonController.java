package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.service.api.SeasonApi;
import ru.qwonix.empioner.telegram.service.api.graphql.model.SeasonInput;
import ru.qwonix.empioner.telegram.service.api.mapper.SeasonMapper;

@DgsComponent
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonApi seasonApi;
    private final SeasonMapper mapper;

    @DgsQuery
    public Mono<SeasonInput> seasonById(@InputArgument SeasonId id) {
        return Mono.fromCallable(() -> seasonApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @DgsQuery
    public Flux<SeasonInput> seasonsBySeriesId(@InputArgument SeriesId id, @InputArgument Integer limit, @InputArgument Integer page) {
        return Flux.defer(() -> Flux.fromIterable(seasonApi.findAllBySeriesIdOrderByNumberWithLimitAndPage(id, limit, page)))
                .map(mapper::toInput);
    }

    @DgsQuery
    public Mono<Integer> countSeasonsBySeriesId(@InputArgument SeriesId id) {
        return Mono.fromCallable(() -> seasonApi.countAllBySeries(id));
    }
}
