package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.SeriesApi;
import ru.qwonix.empioner.telegram.service.api.graphql.api.SeriesQueryResolver;
import ru.qwonix.empioner.telegram.service.api.graphql.model.SeriesInput;
import ru.qwonix.empioner.telegram.service.api.mapper.SeriesMapper;

@Controller
@RequiredArgsConstructor
public class SeriesController implements SeriesQueryResolver {

    private final SeriesApi seriesApi;
    private final SeriesMapper mapper;

    @QueryMapping
    @Override
    public Mono<SeriesInput> getSeriesById(@Argument SeriesId id) {
        return Mono.fromCallable(() -> seriesApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    @Override
    public Flux<SeriesInput> getSeriesByShowId(@Argument ShowId id) {
        return Flux.defer(() -> Flux.fromIterable(seriesApi.findAllByShowId(id)))
                .map(mapper::toInput);
    }
}