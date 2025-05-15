package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.dgs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.bot.config.coercing.SeriesIdCoercing;
import ru.qwonix.empioner.telegram.bot.config.coercing.ShowIdCoercing;
import ru.qwonix.empioner.telegram.bot.spi.SeriesSpi;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.graphql.api.GetSeriesByIdGraphQLQuery;
import ru.qwonix.empioner.telegram.service.api.graphql.api.GetSeriesByIdProjectionRoot;
import ru.qwonix.empioner.telegram.service.api.graphql.api.GetSeriesByShowIdGraphQLQuery;
import ru.qwonix.empioner.telegram.service.api.graphql.api.GetSeriesByShowIdProjectionRoot;

import java.util.List;
import java.util.Optional;

@Primary
@Component
@RequiredArgsConstructor
public class DgsClientSeriesSpi implements SeriesSpi {

    private final DgsGraphQlClient dgsClient;

    private final SeriesIdCoercing seriesIdCoercing;
    private final ShowIdCoercing showIdCoercing;

    @Override
    public Optional<Series> findById(SeriesId seriesId) {
        return Optional.ofNullable(dgsClient.request(GetSeriesByIdGraphQLQuery.newRequest()
                        .id(seriesId)
                        .build())
                .coercing(SeriesId.class, seriesIdCoercing)
                .projection(new GetSeriesByIdProjectionRoot<>()
                        .id()
                        .title()
                        .description()
                        .imageId()
                        .showId()
                        .priority()
                        .isAvailable())
                .retrieveSync()
                .toEntity(Series.class));
    }

    @Override
    public List<Series> findAllByShowId(ShowId id) {
        return dgsClient.request(GetSeriesByShowIdGraphQLQuery.newRequest()
                        .id(id)
                        .build())
                .coercing(ShowId.class, showIdCoercing)
                .projection(new GetSeriesByShowIdProjectionRoot<>()
                        .id()
                        .title()
                        .description()
                        .imageId()
                        .showId()
                        .priority()
                        .isAvailable())
                .retrieveSync()
                .toEntityList(Series.class);
    }
}
