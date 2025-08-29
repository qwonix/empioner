package ru.qwonix.empioner.telegram.bot.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.bot.api.VideoApi;
import ru.qwonix.empioner.telegram.bot.telegram.config.TelegramProperties;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TelegramVideoService {
    private final VideoApi videoApi;
    private final TelegramProperties telegramProperties;

    public List<Video> findAllVideoInGroup(VideoGroupId videoGroupId) {
        return videoApi.findAllByVideoGroupId(videoGroupId);
    }

    public Optional<Video> findById(VideoId videoId) {
        return videoApi.findById(videoId);
    }

    public Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId) {
        return videoApi.findMaxPriorityInGroup(videoGroupId);
    }

    public String createText(Video video) {
        return "||*Предоставлено @" + telegramProperties.username().replaceAll("_", "\\\\_") + "*||";
    }
}
