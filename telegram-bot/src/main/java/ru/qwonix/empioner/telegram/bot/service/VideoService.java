package ru.qwonix.empioner.telegram.bot.service;

import ru.qwonix.empioner.telegram.bot.entity.Video;
import ru.qwonix.empioner.telegram.bot.entity.id.VideoGroupId;
import ru.qwonix.empioner.telegram.bot.entity.id.VideoId;

import java.util.List;
import java.util.Optional;

public interface VideoService {
    Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId);

    Optional<Video> findById(VideoId videoId);

    List<Video> findAllByVideoGroupId(VideoGroupId videoGroupId);
}
