package ru.qwonix.empioner.service.dao;

import ru.qwonix.empioner.service.entity.Video;
import ru.qwonix.empioner.service.entity.id.VideoGroupId;
import ru.qwonix.empioner.service.entity.id.VideoId;

import java.util.List;
import java.util.Optional;

public interface VideoDao {
    Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId);

    Optional<Video> findById(VideoId videoId);

    List<Video> findAllByVideoGroupId(VideoGroupId videoGroupId);
}
