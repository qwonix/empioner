package ru.qwonix.empioner.telegram.bot.spi.spring.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.bot.spi.VideoSpi;
import ru.qwonix.empioner.telegram.bot.spi.spring.graphql.model.AddVideoInput;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class GraphQlClientVideoSpi implements VideoSpi {

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

    private static final String CREATE_VIDEO_MUTATION = """
            mutation CreateVideo($video: AddVideoInput!, $needCreateNewVideoGroup: Boolean!) {
               create(video: $video, needCreateNewVideoGroup: $needCreateNewVideoGroup)
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

    @Override
    public VideoId createVideo(AddVideoInput addVideoInput) {
        return graphQlClient.document(CREATE_VIDEO_MUTATION)
                .variable("video", addVideoInput)
                .variable("needCreateNewVideoGroup", true)
                .retrieve("create")
                .toEntity(VideoId.class)
                .block();
    }
}
