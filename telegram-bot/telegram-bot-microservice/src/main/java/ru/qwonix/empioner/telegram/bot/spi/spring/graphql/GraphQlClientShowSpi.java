package ru.qwonix.empioner.telegram.bot.spi.spring.graphql;

import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.bot.spi.ShowSpi;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.id.ShowId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class GraphQlClientShowSpi implements ShowSpi {

    private static final String GET_BY_ID_QUERY = """
            query GetShowById($id: ShowId!) {
                getShowById(id: $id) {
                    id
                    title
                    description
                    imageId
                    priority
                    isAvailable
                }
            }
            """;
    private static final String FIND_ALL_QUERY = """
            query GetAllShows($limit: Int, $page: Int) {
                getAllShows(limit: $limit, page: $page) {
                    id
                    title
                    description
                    imageId
                    priority
                    isAvailable
                }
            }
            """;
    private final GraphQlClient graphQlClient;

    public GraphQlClientShowSpi(GraphQlClient graphQlClient) {
        this.graphQlClient = graphQlClient;
    }

    @Override
    public Optional<Show> findById(ShowId showId) {
        return graphQlClient.document(GET_BY_ID_QUERY)
                .variable("id", showId.value())
                .retrieve("getShowById")
                .toEntity(Show.class)
                .blockOptional();
    }

    @Override
    public List<Show> findAllOrderByNumberWithLimitAndPage(int limit, int page) {
        return graphQlClient.document(FIND_ALL_QUERY)
                .variables(Map.of(
                        "limit", limit,
                        "page", page
                ))
                .retrieve("getAllShows")
                .toEntityList(Show.class)
                .block();
    }
}
