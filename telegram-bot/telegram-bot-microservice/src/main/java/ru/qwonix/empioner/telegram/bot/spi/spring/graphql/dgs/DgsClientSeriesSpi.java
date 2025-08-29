package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.dgs;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.bot.config.coercing.SeriesIdCoercing;
import ru.qwonix.empioner.telegram.bot.config.coercing.ShowIdCoercing;
import ru.qwonix.empioner.telegram.bot.spi.SeriesSpi;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.graphql.api.SeriesByIdGraphQLQuery;
import ru.qwonix.empioner.telegram.service.api.graphql.api.SeriesByIdProjectionRoot;
import ru.qwonix.empioner.telegram.service.api.graphql.api.SeriesByShowIdGraphQLQuery;
import ru.qwonix.empioner.telegram.service.api.graphql.api.SeriesByShowIdProjectionRoot;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DgsClientSeriesSpi implements SeriesSpi {

    private final DgsGraphQlClient dgsClient;

    private final SeriesIdCoercing seriesIdCoercing;
    private final ShowIdCoercing showIdCoercing;

    @Override
    public Optional<Series> findById(SeriesId seriesId) {
        return Optional.ofNullable(dgsClient.request(SeriesByIdGraphQLQuery.newRequest()
                        .id(seriesId)
                        .build())
                .coercing(SeriesId.class, seriesIdCoercing)
                .projection(new SeriesByIdProjectionRoot<>()
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
        return dgsClient.request(SeriesByShowIdGraphQLQuery.newRequest()
                        .id(id)
                        .build())
                .coercing(ShowId.class, showIdCoercing)
                .projection(new SeriesByShowIdProjectionRoot<>()
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
