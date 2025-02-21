package ru.qwonix.empioner.telegram.bot.spi.spring.graphql;

import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.bot.spi.SeriesSpi;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;

import java.util.List;
import java.util.Optional;

@Repository
public class GraphQlClientSeriesSpi implements SeriesSpi {

    private static final String GET_BY_ID_QUERY = """
            query GetSeriesById($id: SeriesId!) {
                getSeriesById(id: $id) {
                    id
                    title
                    description
                    showId
                    imageId
                    priority
                    isAvailable
                }
            }
            """;
    private static final String FIND_BY_SHOW_QUERY = """
            query GetSeriesByShowId($id: ShowId!) {
                getSeriesByShowId(id: $id) {
                    id
                    title
                    description
                    showId
                    imageId
                    priority
                    isAvailable
                }
            }
            """;
    private final GraphQlClient graphQlClient;

    public GraphQlClientSeriesSpi(GraphQlClient graphQlClient) {
        this.graphQlClient = graphQlClient;
    }

    @Override
    public Optional<Series> findById(SeriesId seriesId) {
        return Optional.ofNullable(
                graphQlClient.document(GET_BY_ID_QUERY)
                        .variable("id", seriesId.value())
                        .retrieve("getSeriesById")
                        .toEntity(Series.class)
                        .block()
        );
    }

    @Override
    public List<Series> findAllByShowId(ShowId showId) {
        return graphQlClient.document(FIND_BY_SHOW_QUERY)
                .variable("id", showId.value())
                .retrieve("getSeriesByShowId")
                .toEntityList(Series.class)
                .block();
    }
}
