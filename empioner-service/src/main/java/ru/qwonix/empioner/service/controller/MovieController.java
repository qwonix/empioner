package ru.qwonix.empioner.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.service.entity.Movie;
import ru.qwonix.empioner.service.entity.id.MovieId;
import ru.qwonix.empioner.service.entity.id.ShowId;
import ru.qwonix.empioner.service.entity.id.VideoGroupId;
import ru.qwonix.empioner.service.service.MovieService;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @QueryMapping
    public Mono<Movie> getMovieById(@Argument MovieId id) {
        return Mono.fromCallable(() -> movieService.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Movie> getMoviesByShowId(@Argument ShowId id) {
        return Flux.defer(() -> Flux.fromIterable(movieService.findAllByShowId(id)));
    }

    @QueryMapping
    public Mono<Movie> getMovieByVideoGroupId(@Argument VideoGroupId id) {
        return Mono.fromCallable(() -> movieService.findByVideoId(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }
}
