package ru.qwonix.empioner.telegram.bot.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.bot.dao.EpisodeDao;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class EpisodeGraphQlClientDao implements EpisodeDao {

    private static final String GET_BY_ID_QUERY = """
            query GetEpisodeById($id: EpisodeId!) {
                getEpisodeById(id: $id) {
                    id
                    title
                    description
                    number
                    previousEpisodeId
                    nextEpisodeId
                    videoGroupId
                    seasonId
                    imageId
                    isAvailable
                }
            }
            """;
    private static final String COUNT_BY_SEASON_QUERY = """
            query CountEpisodesBySeasonId($id: SeasonId!) {
                countEpisodesBySeasonId(id: $id)
            }
            """;
    private static final String FIND_BY_SEASON_QUERY = """
            query GetEpisodesBySeasonId($id: SeasonId!, $limit: Int, $page: Int) {
                getEpisodesBySeasonId(id: $id, limit: $limit, page: $page) {
                    id
                    title
                    description
                    number
                    previousEpisodeId
                    nextEpisodeId
                    videoGroupId
                    seasonId
                    imageId
                    isAvailable
                }
            }
            """;
    private static final String FIND_BY_VIDEO_GROUP_QUERY = """
            query GetEpisodeByVideoGroupId($id: VideoGroupId!) {
                getEpisodeByVideoGroupId(id: $id) {
                    id
                    title
                    description
                    number
                    previousEpisodeId
                    nextEpisodeId
                    videoGroupId
                    seasonId
                    imageId
                    isAvailable
                }
            }
            """;
    private static final String CHANGE_AVAILABLE = """
            mutation ChangeAvailable($id: EpisodeId!, $isAvailable: Boolean!) {
                changeAvailable(id: $id, isAvailable: $isAvailable)
            }
            """;
    private final GraphQlClient graphQlClient;

    @Override
    public Optional<Episode> findById(EpisodeId id) {
        return graphQlClient.document(GET_BY_ID_QUERY)
                .variable("id", id.value().toString())
                .retrieve("getEpisodeById")
                .toEntity(Episode.class)
                .blockOptional();
    }

    @Override
    public Integer countAllBySeasonId(SeasonId id) {
        return graphQlClient.document(COUNT_BY_SEASON_QUERY)
                .variable("id", id.value().toString())
                .retrieve("countEpisodesBySeasonId")
                .toEntity(Integer.class)
                .defaultIfEmpty(0)
                .block();
    }

    @Override
    public Integer countAllAvailableBySeasonId(SeasonId seasonId) {
        return this.countAllBySeasonId(seasonId);
    }

    @Override
    public List<Episode> findAllBySeasonIdOrderByNumberWithLimitAndPage(SeasonId id, int limit, int page) {
        return graphQlClient.document(FIND_BY_SEASON_QUERY)
                .variables(Map.of(
                        "id", id.value().toString(),
                        "limit", limit,
                        "page", page
                ))
                .retrieve("getEpisodesBySeasonId")
                .toEntityList(Episode.class)
                .block();
    }

    @Override
    public Optional<Episode> findByVideoGroupId(VideoGroupId id) {
        return graphQlClient.document(FIND_BY_VIDEO_GROUP_QUERY)
                .variable("id", id.value().toString())
                .retrieve("getEpisodeByVideoGroupId")
                .toEntity(Episode.class)
                .blockOptional();
    }

    @Override
    public Boolean changeAvailable(EpisodeId id, Boolean isAvailable) {
        return graphQlClient.document(CHANGE_AVAILABLE)
                .variables(Map.of(
                        "id", id.value().toString(),
                        "isAvailable", isAvailable
                ))
                .retrieve("changeAvailable")
                .toEntity(Boolean.class)
                .block();
    }
}
