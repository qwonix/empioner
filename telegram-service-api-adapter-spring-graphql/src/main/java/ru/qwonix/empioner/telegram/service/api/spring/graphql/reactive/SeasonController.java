package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.service.api.SeasonApi;

@Controller
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonApi seasonApi;

    @QueryMapping
    public Mono<Season> getSeasonById(@Argument SeasonId id) {
        return Mono.fromCallable(() -> seasonApi.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Season> getSeasonsBySeriesId(@Argument SeriesId id, @Argument int limit, @Argument int page) {
        return Flux.defer(() -> Flux.fromIterable(seasonApi.findAllBySeriesIdOrderByNumberWithLimitAndPage(id, limit, page)));
    }

    @QueryMapping
    public Mono<Integer> countSeasonsBySeriesId(@Argument SeriesId id) {
        return Mono.fromCallable(() -> seasonApi.countAllBySeries(id));
    }
}
