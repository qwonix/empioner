package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.dgs;

import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.bot.config.coercing.SeasonIdCoercing;
import ru.qwonix.empioner.telegram.bot.config.coercing.SeriesIdCoercing;
import ru.qwonix.empioner.telegram.bot.spi.SeasonSpi;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.service.api.graphql.api.*;

import java.util.List;
import java.util.Optional;

@Primary
@Component
@RequiredArgsConstructor
public class DgsClientSeasonSpi implements SeasonSpi {

    private final DgsGraphQlClient dgsClient;

    private final SeasonIdCoercing seasonIdCoercing;
    private final SeriesIdCoercing seriesIdCoercing;

    @Override
    public Optional<Season> findById(SeasonId seasonId) {
        return Optional.ofNullable(dgsClient.request(GetSeasonByIdGraphQLQuery.newRequest()
                        .id(seasonId)
                        .build())
                .coercing(SeasonId.class, seasonIdCoercing)
                .projection(new GetSeasonByIdProjectionRoot<>()
                        .id()
                        .number()
                        .description()
                        .seriesId()
                        .imageId()
                        .isAvailable())
                .retrieveSync()
                .toEntity(Season.class));
    }

    @Override
    public int countAllBySeries(SeriesId seriesId) {
        return dgsClient.request(CountSeasonsBySeriesIdGraphQLQuery.newRequest()
                        .id(seriesId)
                        .build())
                .coercing(SeriesId.class, seriesIdCoercing)
                .projection(new BaseProjectionNode() {
                })
                .retrieveSync()
                .toEntity(Integer.class);
    }

    @Override
    public List<Season> findAllBySeriesIdOrderByNumberWithLimitAndPage(SeriesId seriesId, int limit, int page) {
        return dgsClient.request(GetSeasonsBySeriesIdGraphQLQuery.newRequest()
                        .id(seriesId)
                        .page(page)
                        .limit(limit)
                        .build())
                .coercing(SeriesId.class, seriesIdCoercing)
                .projection(new GetSeasonsBySeriesIdProjectionRoot<>()
                        .id()
                        .number()
                        .description()
                        .seriesId()
                        .imageId()
                        .isAvailable())
                .retrieveSync()
                .toEntityList(Season.class);
    }
}
