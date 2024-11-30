package ru.qwonix.empioner.bot.dao;

import ru.qwonix.empioner.bot.entity.Video;
import ru.qwonix.empioner.bot.entity.id.VideoGroupId;
import ru.qwonix.empioner.bot.entity.id.VideoId;

import java.util.List;
import java.util.Optional;

public interface VideoDao {
    Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId);

    Optional<Video> findById(VideoId videoId);

    List<Video> findAllByVideoGroupId(VideoGroupId videoGroupId);
}
