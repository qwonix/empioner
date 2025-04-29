package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.ShowApi;
import ru.qwonix.empioner.telegram.service.api.graphql.api.ShowQueryResolver;
import ru.qwonix.empioner.telegram.service.api.graphql.model.ShowInput;
import ru.qwonix.empioner.telegram.service.api.mapper.ShowMapper;

@Controller
@RequiredArgsConstructor
public class ShowController implements ShowQueryResolver {

    private final ShowApi showApi;
    private final ShowMapper mapper;

    @QueryMapping
    @Override
    public Mono<ShowInput> getShowById(@Argument ShowId id) {
        return Mono.fromCallable(() -> showApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    @Override
    public Flux<ShowInput> getAllShows(@Argument Integer limit, @Argument Integer page) {
        return Flux.defer(() -> Flux.fromIterable(showApi.findAllOrderByNumberWithLimitAndPage(limit, page)))
                .map(mapper::toInput);
    }
}