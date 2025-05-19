package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.SeriesApi;
import ru.qwonix.empioner.telegram.service.api.graphql.model.SeriesInput;
import ru.qwonix.empioner.telegram.service.api.mapper.SeriesMapper;

@DgsComponent
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesApi seriesApi;
    private final SeriesMapper mapper;

    @DgsQuery
    public Mono<SeriesInput> seriesById(@InputArgument SeriesId id) {
        return Mono.fromCallable(() -> seriesApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @DgsQuery
    public Flux<SeriesInput> seriesByShowId(@InputArgument ShowId id) {
        return Flux.defer(() -> Flux.fromIterable(seriesApi.findAllByShowId(id)))
                .map(mapper::toInput);
    }
}