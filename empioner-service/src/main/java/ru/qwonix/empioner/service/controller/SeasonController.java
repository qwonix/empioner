package ru.qwonix.empioner.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.service.entity.Season;
import ru.qwonix.empioner.service.entity.id.SeasonId;
import ru.qwonix.empioner.service.entity.id.SeriesId;
import ru.qwonix.empioner.service.service.SeasonService;

@Controller
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonService seasonService;

    @QueryMapping
    public Mono<Season> getSeasonById(@Argument SeasonId id) {
        return Mono.fromCallable(() -> seasonService.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Season> getSeasonsBySeriesId(@Argument SeriesId id, @Argument int limit, @Argument int page) {
        return Flux.defer(() -> Flux.fromIterable(seasonService.findAllBySeriesIdOrderByNumberWithLimitAndPage(id, limit, page)));
    }

    @QueryMapping
    public Mono<Integer> countSeasonsBySeriesId(@Argument SeriesId id) {
        return Mono.fromCallable(() -> seasonService.countAllBySeries(id));
    }
}
