package ru.qwonix.empioner.telegram.bot.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.bot.dao.VideoDao;
import ru.qwonix.empioner.telegram.bot.entity.Video;
import ru.qwonix.empioner.telegram.bot.entity.id.VideoGroupId;
import ru.qwonix.empioner.telegram.bot.entity.id.VideoId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class VideoGraphQlClientDao implements VideoDao {

    private static final String GET_BY_ID_QUERY = """
            query GetVideoById($id: VideoId!) {
                getVideoById(id: $id) {
                    id
                    videoGroupId
                    description
                    telegramFileId
                    telegramFileUniqueId
                    priority
                    isAvailable
                }
            }
            """;
    private static final String GET_MAX_PRIORITY_QUERY = """
            query GetMaxPriorityVideo($id: VideoGroupId!) {
                getMaxPriorityVideo(id: $id) {
                    id
                    videoGroupId
                    description
                    telegramFileId
                    telegramFileUniqueId
                    priority
                    isAvailable
                }
            }
            """;
    private static final String GET_ALL_BY_GROUP_QUERY = """
            query GetVideosByGroup($id: VideoGroupId!) {
                getVideosByGroup(id: $id) {
                    id
                    videoGroupId
                    description
                    telegramFileId
                    telegramFileUniqueId
                    priority
                    isAvailable
                }
            }
            """;
    private final GraphQlClient graphQlClient;

    @Override
    public Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId) {
        return Optional.ofNullable(
                graphQlClient.document(GET_MAX_PRIORITY_QUERY)
                        .variable("id", videoGroupId.value().toString())
                        .retrieve("getMaxPriorityVideo")
                        .toEntity(Video.class)
                        .block()
        );
    }

    @Override
    public Optional<Video> findById(VideoId videoId) {
        return Optional.ofNullable(
                graphQlClient.document(GET_BY_ID_QUERY)
                        .variable("id", videoId.value().toString())
                        .retrieve("getVideoById")
                        .toEntity(Video.class)
                        .block()
        );
    }

    @Override
    public List<Video> findAllByVideoGroupId(VideoGroupId videoGroupId) {
        return graphQlClient.document(GET_ALL_BY_GROUP_QUERY)
                .variable("id", videoGroupId.value().toString())
                .retrieve("getVideosByGroup")
                .toEntityList(Video.class)
                .block();
    }
}
