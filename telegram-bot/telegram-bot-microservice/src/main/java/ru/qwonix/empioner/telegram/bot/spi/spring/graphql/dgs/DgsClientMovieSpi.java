package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.dgs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.bot.config.coercing.MovieIdCoercing;
import ru.qwonix.empioner.telegram.bot.config.coercing.ShowIdCoercing;
import ru.qwonix.empioner.telegram.bot.config.coercing.VideoGroupIdCoercing;
import ru.qwonix.empioner.telegram.bot.spi.MovieSpi;
import ru.qwonix.empioner.telegram.entity.Movie;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.api.graphql.api.*;

import java.util.List;
import java.util.Optional;

@Primary
@Component
@RequiredArgsConstructor
public class DgsClientMovieSpi implements MovieSpi {

    private final DgsGraphQlClient dgsClient;

    private final MovieIdCoercing movieIdCoercing;
    private final ShowIdCoercing showIdCoercing;
    private final VideoGroupIdCoercing videoGroupIdCoercing;

    @Override
    public Optional<Movie> findById(MovieId id) {
        return Optional.ofNullable(dgsClient.request(GetMovieByIdGraphQLQuery.newRequest()
                        .id(id)
                        .build())
                .coercing(MovieId.class, movieIdCoercing)
                .projection(new GetMovieByIdProjectionRoot<>()
                        .id()
                        .title()
                        .description()
                        .showId()
                        .videoGroupId()
                        .imageId()
                        .priority()
                        .isAvailable())
                .retrieveSync()
                .toEntity(Movie.class));
    }

    @Override
    public List<Movie> findAllByShowId(ShowId id) {
        return dgsClient.request(GetMoviesByShowIdGraphQLQuery.newRequest()
                        .id(id)
                        .build())
                .coercing(MovieId.class, movieIdCoercing)
                .coercing(ShowId.class, showIdCoercing)
                .projection(new GetMoviesByShowIdProjectionRoot<>()
                        .id()
                        .title()
                        .description()
                        .showId()
                        .videoGroupId()
                        .imageId()
                        .priority()
                        .isAvailable())
                .retrieveSync()
                .toEntityList(Movie.class);
    }

    @Override
    public Optional<Movie> findByVideoId(VideoGroupId id) {
        return Optional.ofNullable(dgsClient.request(GetMovieByVideoGroupIdGraphQLQuery.newRequest()
                        .id(id)
                        .build())
                .coercing(MovieId.class, movieIdCoercing)
                .coercing(VideoGroupId.class, videoGroupIdCoercing)
                .projection(new GetMovieByVideoGroupIdProjectionRoot<>()
                        .id()
                        .title()
                        .description()
                        .showId()
                        .videoGroupId()
                        .imageId()
                        .priority()
                        .isAvailable())
                .retrieveSync()
                .toEntity(Movie.class));
    }
}
