package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.ShowApi;
import ru.qwonix.empioner.telegram.service.api.graphql.model.ShowInput;
import ru.qwonix.empioner.telegram.service.api.mapper.ShowMapper;

@DgsComponent
@RequiredArgsConstructor
public class ShowController {

    private final ShowApi showApi;
    private final ShowMapper mapper;

    @DgsQuery
    public Mono<ShowInput> getShowById(@InputArgument ShowId id) {
        return Mono.fromCallable(() -> showApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @DgsQuery
    public Flux<ShowInput> getAllShows(@InputArgument Integer limit, @InputArgument Integer page) {
        return Flux.defer(() -> Flux.fromIterable(showApi.findAllOrderByNumberWithLimitAndPage(limit, page)))
                .map(mapper::toInput);
    }
}