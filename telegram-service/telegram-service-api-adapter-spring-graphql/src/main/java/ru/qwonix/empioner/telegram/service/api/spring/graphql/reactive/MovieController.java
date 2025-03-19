package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.entity.Movie;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.api.movieApi;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final movieApi movieApi;

    @QueryMapping
    public Mono<Movie> getMovieById(@Argument MovieId id) {
        return Mono.fromCallable(() -> movieApi.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    public Flux<Movie> getMoviesByShowId(@Argument ShowId id) {
        return Flux.defer(() -> Flux.fromIterable(movieApi.findAllByShowId(id)));
    }

    @QueryMapping
    public Mono<Movie> getMovieByVideoGroupId(@Argument VideoGroupId id) {
        return Mono.fromCallable(() -> movieApi.findByVideoId(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }
}
