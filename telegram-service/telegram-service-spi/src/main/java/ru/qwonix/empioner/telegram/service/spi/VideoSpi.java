package ru.qwonix.empioner.telegram.service.spi;

import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.entity.VideoDetails;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;

import java.util.List;
import java.util.Optional;

public interface VideoSpi {
    Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId);

    Optional<Video> findById(VideoId videoId);

    List<Video> findAllByVideoGroupId(VideoGroupId videoGroupId);

    VideoGroupId createVideoGroup();

    VideoId createVideo(VideoGroupId videoGroupId, Video video);

    void createVideoDetails(VideoDetails videoDetails);

    void updateTelegramFileIdByTelegramFileUniqueId(
            TelegramFileUniqueId telegramFileUniqueId,
            TelegramFileId telegramFileId);
}
