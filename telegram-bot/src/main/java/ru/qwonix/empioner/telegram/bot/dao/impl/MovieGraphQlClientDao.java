package ru.qwonix.empioner.telegram.bot.dao.impl;

import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.bot.dao.MovieDao;
import ru.qwonix.empioner.telegram.entity.Movie;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;

import java.util.List;
import java.util.Optional;

@Repository
public class MovieGraphQlClientDao implements MovieDao {

    private static final String GET_BY_ID_QUERY = """
            query GetMovieById($id: MovieId!) {
                getMovieById(id: $id) {
                    id
                    title
                    description
                    showId
                    imageId
                    videoGroupId
                    priority
                    isAvailable
                }
            }
            """;
    private static final String FIND_BY_SHOW_ID_QUERY = """
            query GetMoviesByShowId($id: ShowId!) {
                getMoviesByShowId(id: $id) {
                    id
                    title
                    description
                    showId
                    imageId
                    videoGroupId
                    priority
                    isAvailable
                }
            }
            """;
    private static final String FIND_BY_VIDEO_GROUP_QUERY = """
            query GetMovieByVideoGroupId($id: VideoGroupId!) {
                getMovieByVideoGroupId(id: $id) {
                    id
                    title
                    description
                    showId
                    imageId
                    videoGroupId
                    priority
                    isAvailable
                }
            }
            """;
    private final GraphQlClient graphQlClient;

    public MovieGraphQlClientDao(GraphQlClient graphQlClient) {
        this.graphQlClient = graphQlClient;
    }

    @Override
    public Optional<Movie> findById(MovieId id) {
        return Optional.ofNullable(
                graphQlClient.document(GET_BY_ID_QUERY)
                        .variable("id", id.value())
                        .retrieve("getMovieById")
                        .toEntity(Movie.class)
                        .block()
        );
    }

    @Override
    public List<Movie> findAllByShowId(ShowId id) {
        return graphQlClient.document(FIND_BY_SHOW_ID_QUERY)
                .variable("id", id.value())
                .retrieve("getMoviesByShowId")
                .toEntityList(Movie.class)
                .block();
    }

    @Override
    public Optional<Movie> findByVideoId(VideoGroupId id) {
        return Optional.ofNullable(
                graphQlClient.document(FIND_BY_VIDEO_GROUP_QUERY)
                        .variable("id", id.value())
                        .retrieve("getMovieByVideoGroupId")
                        .toEntity(Movie.class)
                        .block()
        );
    }
}