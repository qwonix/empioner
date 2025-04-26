package ru.qwonix.empioner.telegram.bot.spi.spring.graphql;

import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.bot.spi.SeasonSpi;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.SeriesId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class GraphQlClientSeasonSpi implements SeasonSpi {

    private static final String GET_BY_ID_QUERY = """
            query GetSeasonById($id: SeasonId!) {
                getSeasonById(id: $id) {
                    id
                    description
                    number
                    seriesId
                    imageId
                    isAvailable
                }
            }
            """;
    private static final String COUNT_BY_SERIES_QUERY = """
            query CountSeasonsBySeriesId($id: SeriesId!) {
                countSeasonsBySeriesId(id: $id)
            }
            """;
    private static final String FIND_BY_SERIES_QUERY = """
            query GetSeasonsBySeriesId($id: SeriesId!, $limit: Int, $page: Int) {
                getSeasonsBySeriesId(id: $id, limit: $limit, page: $page) {
                    id
                    description
                    number
                    seriesId
                    imageId
                    isAvailable
                }
            }
            """;
    private final GraphQlClient graphQlClient;

    public GraphQlClientSeasonSpi(GraphQlClient graphQlClient) {
        this.graphQlClient = graphQlClient;
    }

    @Override
    public Optional<Season> findById(SeasonId seasonId) {
        return Optional.ofNullable(
                graphQlClient.document(GET_BY_ID_QUERY)
                        .variable("id", seasonId.value())
                        .retrieve("getSeasonById")
                        .toEntity(Season.class)
                        .block()
        );
    }

    @Override
    public int countAllBySeries(SeriesId seriesId) {
        return graphQlClient.document(COUNT_BY_SERIES_QUERY)
                .variable("id", seriesId.value())
                .retrieve("countSeasonsBySeriesId")
                .toEntity(Integer.class)
                .defaultIfEmpty(0)
                .block();
    }

    @Override
    public List<Season> findAllBySeriesIdOrderByNumberWithLimitAndPage(SeriesId seriesId, int limit, int page) {
        return graphQlClient.document(FIND_BY_SERIES_QUERY)
                .variables(Map.of(
                        "id", seriesId.value(),
                        "limit", limit,
                        "page", page
                ))
                .retrieve("getSeasonsBySeriesId")
                .toEntityList(Season.class)
                .block();
    }
}
