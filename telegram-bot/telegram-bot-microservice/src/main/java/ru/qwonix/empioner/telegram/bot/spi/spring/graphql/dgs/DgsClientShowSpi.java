package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.dgs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.bot.config.coercing.ShowIdCoercing;
import ru.qwonix.empioner.telegram.bot.spi.ShowSpi;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.graphql.api.GetAllShowsGraphQLQuery;
import ru.qwonix.empioner.telegram.service.api.graphql.api.GetShowByIdGraphQLQuery;
import ru.qwonix.empioner.telegram.service.api.graphql.api.GetShowByIdProjectionRoot;

import java.util.List;
import java.util.Optional;

@Primary
@Component
@RequiredArgsConstructor
public class DgsClientShowSpi implements ShowSpi {

    private final DgsGraphQlClient dgsClient;

    private final ShowIdCoercing showIdCoercing;

    @Override
    public Optional<Show> findById(ShowId showId) {
        return Optional.ofNullable(dgsClient.request(GetShowByIdGraphQLQuery.newRequest()
                        .id(showId)
                        .build())
                .coercing(ShowId.class, showIdCoercing)
                .projection(new GetShowByIdProjectionRoot<>()
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
        return dgsClient.request(GetAllShowsGraphQLQuery.newRequest()
                        .limit(limit)
                        .page(page)
                        .build())
                .coercing(ShowId.class, showIdCoercing)
                .projection(new GetShowByIdProjectionRoot<>()
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
