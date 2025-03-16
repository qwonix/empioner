package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.SeriesApi;

@Controller
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesApi seriesApi;

    @QueryMapping
    public Mono<Series> getSeriesById(@Argument SeriesId id) {
        return Mono.fromCallable(() -> seriesApi.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Series> getSeriesByShowId(@Argument ShowId id) {
        return Flux.defer(() -> Flux.fromIterable(seriesApi.findAllByShowId(id)));
    }
}