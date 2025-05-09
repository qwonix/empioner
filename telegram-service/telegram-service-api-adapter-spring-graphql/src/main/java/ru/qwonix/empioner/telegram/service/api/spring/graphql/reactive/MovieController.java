package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.api.graphql.model.MovieInput;
import ru.qwonix.empioner.telegram.service.api.mapper.MovieMapper;
import ru.qwonix.empioner.telegram.service.api.movieApi;

@DgsComponent
@RequiredArgsConstructor
public class MovieController {

    private final movieApi movieApi;
    private final MovieMapper mapper;

    @DgsQuery
    public Mono<MovieInput> getMovieById(@InputArgument MovieId id) {
        return Mono.fromCallable(() -> movieApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @DgsQuery
    public Flux<MovieInput> getMoviesByShowId(@InputArgument ShowId id) {
        return Flux.defer(() -> Flux.fromIterable(movieApi.findAllByShowId(id)))
                .map(mapper::toInput);
    }

    @DgsQuery
    public Mono<MovieInput> getMovieByVideoGroupId(@InputArgument VideoGroupId id) {
        return Mono.fromCallable(() -> movieApi.findByVideoId(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }
}
