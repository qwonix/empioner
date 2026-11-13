package ru.qwonix.empioner.telegram.service.api;

import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.entity.VideoDetails;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.service.model.EpisodeVideoRequest;
import ru.qwonix.empioner.telegram.service.model.UpdateVideoRequest;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;
import ru.qwonix.empioner.telegram.service.model.CreateVideoRequest;

import java.util.List;
import java.util.Optional;

public interface VideoApi {
    Video addToEpisode(EpisodeId episodeId, EpisodeVideoRequest request);

    Video update(VideoId videoId, UpdateVideoRequest request);

    Optional<VideoDetails> findDetails(VideoId videoId);

    Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId);

    Optional<Video> findById(VideoId videoId);

    List<Video> findAllByVideoGroupId(VideoGroupId videoGroupId);

    boolean updateTelegramFileIdByTelegramFileUniqueId(
            TelegramFileUniqueId telegramFileUniqueId,
            TelegramFileId telegramFileId);

    VideoId create(CreateVideoRequest video, boolean needCreateNewVideoGroup);
}
