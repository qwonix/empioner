package ru.qwonix.empioner.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.service.entity.Series;
import ru.qwonix.empioner.service.entity.id.SeriesId;
import ru.qwonix.empioner.service.entity.id.ShowId;
import ru.qwonix.empioner.service.service.SeriesService;

@Controller
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    @QueryMapping
    public Mono<Series> getSeriesById(@Argument SeriesId id) {
        return Mono.fromCallable(() -> seriesService.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Series> getSeriesByShowId(@Argument ShowId id) {
        return Flux.defer(() -> Flux.fromIterable(seriesService.findAllByShowId(id)));
    }
}