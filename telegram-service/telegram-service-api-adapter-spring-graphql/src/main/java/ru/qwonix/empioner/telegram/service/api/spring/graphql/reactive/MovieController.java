package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.api.graphql.api.MovieQueryResolver;
import ru.qwonix.empioner.telegram.service.api.graphql.model.MovieInput;
import ru.qwonix.empioner.telegram.service.api.mapper.MovieMapper;
import ru.qwonix.empioner.telegram.service.api.movieApi;

@Controller
@RequiredArgsConstructor
public class MovieController implements MovieQueryResolver {

    private final movieApi movieApi;
    private final MovieMapper mapper;

    @QueryMapping
    @Override
    public Mono<MovieInput> getMovieById(@Argument MovieId id) {
        return Mono.fromCallable(() -> movieApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @QueryMapping
    @Override
    public Flux<MovieInput> getMoviesByShowId(@Argument ShowId id) {
        return Flux.defer(() -> Flux.fromIterable(movieApi.findAllByShowId(id)))
                .map(mapper::toInput);
    }

    @QueryMapping
    @Override
    public Mono<MovieInput> getMovieByVideoGroupId(@Argument VideoGroupId id) {
        return Mono.fromCallable(() -> movieApi.findByVideoId(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }
}
