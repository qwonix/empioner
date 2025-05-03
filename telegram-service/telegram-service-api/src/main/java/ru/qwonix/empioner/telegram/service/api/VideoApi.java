package ru.qwonix.empioner.telegram.service.api;

import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;
import ru.qwonix.empioner.telegram.service.model.CreateVideoRequest;

import java.util.List;
import java.util.Optional;

public interface VideoApi {
    Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId);

    Optional<Video> findById(VideoId videoId);

    List<Video> findAllByVideoGroupId(VideoGroupId videoGroupId);

    VideoId create(CreateVideoRequest video, boolean needCreateNewVideoGroup);
}
