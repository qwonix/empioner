package ru.qwonix.empioner.bot.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.bot.entity.Video;
import ru.qwonix.empioner.bot.entity.id.VideoGroupId;
import ru.qwonix.empioner.bot.entity.id.VideoId;
import ru.qwonix.empioner.bot.service.VideoService;
import ru.qwonix.empioner.bot.telegram.config.TelegramProperties;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TelegramVideoService {
    private final VideoService videoService;
    private final TelegramProperties telegramProperties;

    public List<Video> findAllVideoInGroup(VideoGroupId videoGroupId) {
        return videoService.findAllByVideoGroupId(videoGroupId);
    }

    public Optional<Video> findById(VideoId videoId) {
        return videoService.findById(videoId);
    }

    public Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId) {
        return videoService.findMaxPriorityInGroup(videoGroupId);
    }

    public String createText(Video video) {
        return "||*Предоставлено @" + telegramProperties.username().replaceAll("_", "\\\\_") + "*||";
    }
}
