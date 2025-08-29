package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.dgs;

import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.bot.config.coercing.EpisodeIdCoercing;
import ru.qwonix.empioner.telegram.bot.config.coercing.SeasonIdCoercing;
import ru.qwonix.empioner.telegram.bot.config.coercing.VideoGroupIdCoercing;
import ru.qwonix.empioner.telegram.bot.spi.EpisodeSpi;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.service.api.graphql.api.*;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DgsClientEpisodeSpi implements EpisodeSpi {

    private final DgsGraphQlClient dgsClient;

    private final EpisodeIdCoercing episodeIdCoercing;
    private final SeasonIdCoercing seasonIdCoercing;
    private final VideoGroupIdCoercing videoGroupIdCoercing;

    @Override
    public Optional<Episode> findById(EpisodeId id) {
        Episode episode = dgsClient.request(EpisodeByIdGraphQLQuery.newRequest()
                        .id(id)
                        .build())
                .coercing(EpisodeId.class, episodeIdCoercing)
                .projection(new EpisodeByIdProjectionRoot<>()
                        .id()
                        .title()
                        .description()
                        .number()
                        .previousEpisodeId()
                        .nextEpisodeId()
                        .videoGroupId()
                        .seasonId()
                        .isAvailable())
                .retrieveSync()
                .toEntity(Episode.class);
        return Optional.ofNullable(episode);
    }

    @Override
    public Integer countAllBySeasonId(SeasonId seasonId) {
        return dgsClient.request(CountEpisodesBySeasonIdGraphQLQuery.newRequest()
                        .id(seasonId)
                        .build())
                .coercing(SeasonId.class, seasonIdCoercing)
                .projection(new BaseProjectionNode() {
                })
                .retrieveSync()
                .toEntity(Integer.class);
    }

    @Override
    public Integer countAllAvailableBySeasonId(SeasonId seasonId) {
        return this.countAllBySeasonId(seasonId);
    }

    @Override
    public List<Episode> findAllBySeasonIdOrderByNumberWithLimitAndPage(SeasonId seasonId, int keyboardButtonsLimit, int page) {
        return dgsClient.request(EpisodesBySeasonIdGraphQLQuery.newRequest()
                        .id(seasonId)
                        .limit(keyboardButtonsLimit)
                        .page(page)
                        .build())
                .coercing(SeasonId.class, seasonIdCoercing)
                .projection(new EpisodesBySeasonIdProjectionRoot<>()
                        .id()
                        .title()
                        .description()
                        .number()
                        .previousEpisodeId()
                        .nextEpisodeId()
                        .videoGroupId()
                        .seasonId()
                        .isAvailable())
                .retrieveSync()
                .toEntityList(Episode.class);
    }

    @Override
    public Optional<Episode> findByVideoGroupId(VideoGroupId id) {
        Episode episode = dgsClient.request(EpisodeByVideoGroupIdGraphQLQuery.newRequest()
                        .id(id)
                        .build())
                .coercing(VideoGroupId.class, videoGroupIdCoercing)
                .projection(new EpisodeByVideoGroupIdProjectionRoot<>()
                        .id()
                        .title()
                        .description()
                        .number()
                        .previousEpisodeId()
                        .nextEpisodeId()
                        .videoGroupId()
                        .seasonId()
                        .isAvailable())
                .retrieveSync()
                .toEntity(Episode.class);
        return Optional.ofNullable(episode);
    }

    @Override
    public Boolean changeAvailable(EpisodeId id, Boolean isAvailable) {
        return dgsClient.request(ChangeAvailableGraphQLQuery.newRequest()
                        .id(id)
                        .isAvailable(isAvailable)
                        .build())
                .coercing(EpisodeId.class, episodeIdCoercing)
                .projection(new BaseProjectionNode() {
                })
                .retrieveSync()
                .toEntity(Boolean.class);
    }
}
