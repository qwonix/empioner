package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.ShowApi;

@Controller
@RequiredArgsConstructor
public class ShowController {

    private final ShowApi showApi;

    @QueryMapping
    public Mono<Show> getShowById(@Argument ShowId id) {
        return Mono.fromCallable(() -> showApi.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Show> getAllShows(@Argument int limit, @Argument int page) {
        return Flux.defer(() -> Flux.fromIterable(showApi.findAllOrderByNumberWithLimitAndPage(limit, page)));
    }
}