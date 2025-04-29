package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.service.api.SeasonApi;
import ru.qwonix.empioner.telegram.service.api.graphql.api.SeasonQueryResolver;
import ru.qwonix.empioner.telegram.service.api.graphql.model.SeasonInput;
import ru.qwonix.empioner.telegram.service.api.mapper.SeasonMapper;

@Controller
@RequiredArgsConstructor
public class SeasonController implements SeasonQueryResolver {

    private final SeasonApi seasonApi;
    private final SeasonMapper mapper;

    @QueryMapping
    @Override
    public Mono<SeasonInput> getSeasonById(@Argument SeasonId id) {
        return Mono.fromCallable(() -> seasonApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    @Override
    public Flux<SeasonInput> getSeasonsBySeriesId(@Argument SeriesId id, @Argument Integer limit, @Argument Integer page) {
        return Flux.defer(() -> Flux.fromIterable(seasonApi.findAllBySeriesIdOrderByNumberWithLimitAndPage(id, limit, page)))
                .map(mapper::toInput);
    }

    @QueryMapping
    @Override
    public Mono<Integer> countSeasonsBySeriesId(@Argument SeriesId id) {
        return Mono.fromCallable(() -> seasonApi.countAllBySeries(id));
    }
}
