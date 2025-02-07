package ru.qwonix.empioner.telegram.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.service.entity.Show;
import ru.qwonix.empioner.telegram.service.entity.id.ShowId;
import ru.qwonix.empioner.telegram.service.service.ShowService;

@Controller
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @QueryMapping
    public Mono<Show> getShowById(@Argument ShowId id) {
        return Mono.fromCallable(() -> showService.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Show> getAllShows(@Argument int limit, @Argument int page) {
        return Flux.defer(() -> Flux.fromIterable(showService.findAllOrderByNumberWithLimitAndPage(limit, page)));
    }
}