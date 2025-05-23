package ru.qwonix.empioner.telegram.bot.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.bot.api.VideoApi;
import ru.qwonix.empioner.telegram.bot.spi.VideoSpi;
import ru.qwonix.empioner.telegram.bot.spi.spring.graphql.model.AddVideoInput;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VideoUseCase implements VideoApi {

    private final VideoSpi videoDao;

    @Override
    public Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId) {
        return videoDao.findMaxPriorityInGroup(videoGroupId);
    }

    @Override
    public Optional<Video> findById(VideoId videoId) {
        return videoDao.findById(videoId);
    }

    @Override
    public List<Video> findAllByVideoGroupId(VideoGroupId videoGroupId) {
        return videoDao.findAllByVideoGroupId(videoGroupId);
    }

    @Override
    public VideoId createVideo(AddVideoInput addVideoInput) {
        return videoDao.createVideo(addVideoInput);
    }
}
