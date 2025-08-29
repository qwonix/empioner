package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.dgs;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.bot.config.coercing.ShowIdCoercing;
import ru.qwonix.empioner.telegram.bot.spi.ShowSpi;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.graphql.api.ShowByIdGraphQLQuery;
import ru.qwonix.empioner.telegram.service.api.graphql.api.ShowByIdProjectionRoot;
import ru.qwonix.empioner.telegram.service.api.graphql.api.ShowsGraphQLQuery;
import ru.qwonix.empioner.telegram.service.api.graphql.api.ShowsProjectionRoot;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DgsClientShowSpi implements ShowSpi {

    private final DgsGraphQlClient dgsClient;

    private final ShowIdCoercing showIdCoercing;

    @Override
    public Optional<Show> findById(ShowId showId) {
        return Optional.ofNullable(dgsClient.request(ShowByIdGraphQLQuery.newRequest()
                        .id(showId)
                        .build())
                .coercing(ShowId.class, showIdCoercing)
                .projection(new ShowByIdProjectionRoot<>()
                        .id()
                        .title()
                        .description()
                        .isAvailable()
                        .imageId()
                        .priority())
                .retrieveSync()
                .toEntity(Show.class));
    }

    @Override
    public List<Show> findAllOrderByNumberWithLimitAndPage(int limit, int page) {
        return dgsClient.request(ShowsGraphQLQuery.newRequest()
                        .limit(limit)
                        .page(page)
                        .build())
                .coercing(ShowId.class, showIdCoercing)
                .projection(new ShowsProjectionRoot<>()
                        .id()
                        .title()
                        .description()
                        .isAvailable()
                        .imageId()
                        .priority())
                .retrieveSync()
                .toEntityList(Show.class);
    }
}
