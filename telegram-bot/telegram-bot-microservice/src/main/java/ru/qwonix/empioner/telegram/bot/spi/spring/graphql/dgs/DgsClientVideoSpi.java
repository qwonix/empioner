package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.dgs;

import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.bot.config.coercing.TelegramFileIdCoercing;
import ru.qwonix.empioner.telegram.bot.config.coercing.TelegramFileUniqueIdCoercing;
import ru.qwonix.empioner.telegram.bot.config.coercing.VideoGroupIdCoercing;
import ru.qwonix.empioner.telegram.bot.config.coercing.VideoIdCoercing;
import ru.qwonix.empioner.telegram.bot.spi.VideoSpi;
import ru.qwonix.empioner.telegram.bot.spi.spring.graphql.mapper.VideoMapper;
import ru.qwonix.empioner.telegram.bot.spi.spring.graphql.model.AddVideoInput;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;
import ru.qwonix.empioner.telegram.service.api.graphql.api.*;

import java.util.List;
import java.util.Optional;

@Primary
@Component
@RequiredArgsConstructor
public class DgsClientVideoSpi implements VideoSpi {

    private final DgsGraphQlClient dgsClient;

    private final VideoIdCoercing videoIdCoercing;
    private final VideoGroupIdCoercing videoGroupIdCoercing;
    private final TelegramFileIdCoercing telegramFileIdCoercing;
    private final TelegramFileUniqueIdCoercing telegramFileUniqueIdCoercing;

    private final VideoMapper mapper;

    @Override
    public Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId) {
        return Optional.ofNullable(dgsClient.request(MaxPriorityVideoGraphQLQuery.newRequest()
                        .id(videoGroupId)
                        .build())
                .coercing(VideoGroupId.class, videoGroupIdCoercing)
                .projection(new MaxPriorityVideoProjectionRoot<>()
                        .id()
                        .videoGroupId()
                        .description()
                        .telegramFileId()
                        .telegramFileUniqueId()
                        .priority()
                        .isAvailable())
                .retrieveSync()
                .toEntity(Video.class));
    }

    @Override
    public Optional<Video> findById(VideoId videoId) {
        return Optional.ofNullable(dgsClient.request(VideoByIdGraphQLQuery.newRequest()
                        .id(videoId).build())
                .coercing(VideoId.class, videoIdCoercing)
                .projection(new VideoByIdProjectionRoot<>()
                        .id()
                        .description()
                        .videoGroupId()
                        .telegramFileId()
                        .telegramFileUniqueId()
                        .priority()
                        .isAvailable())
                .retrieveSync()
                .toEntity(Video.class));

    }

    @Override
    public List<Video> findAllByVideoGroupId(VideoGroupId videoGroupId) {
        return dgsClient.request(VideosByVideoGroupIdGraphQLQuery.newRequest()
                        .id(videoGroupId).build())
                .coercing(VideoId.class, videoIdCoercing)
                .coercing(VideoGroupId.class, videoGroupIdCoercing)
                .projection(new VideoByIdProjectionRoot<>()
                        .id()
                        .description()
                        .videoGroupId()
                        .telegramFileId()
                        .telegramFileUniqueId()
                        .priority()
                        .isAvailable())
                .retrieveSync()
                .toEntityList(Video.class);
    }

    @Override
    public VideoId createVideo(AddVideoInput addVideoInput) {
        return dgsClient.request(CreateGraphQLQuery.newRequest()
                        .video(mapper.toAddVideoInput(addVideoInput))
                        .build())
                .coercing(TelegramFileId.class, telegramFileIdCoercing)
                .coercing(TelegramFileUniqueId.class, telegramFileUniqueIdCoercing)
                .projection(new BaseProjectionNode() {
                })
                .retrieveSync()
                .toEntity(VideoId.class);
    }

    @Override
    public void updateTelegramFileIdByTelegramFileUniqueId(TelegramFileUniqueId telegramFileUniqueId, TelegramFileId telegramFileId) {
        dgsClient.request(UpdateTelegramFileIdGraphQLQuery.newRequest()
                        .id(telegramFileUniqueId)
                        .newTelegramFileId(telegramFileId)
                        .build())
                .coercing(TelegramFileId.class, telegramFileIdCoercing)
                .coercing(TelegramFileUniqueId.class, telegramFileUniqueIdCoercing)
                .projection(new BaseProjectionNode() {
                })
                .retrieveSync();
    }
}
