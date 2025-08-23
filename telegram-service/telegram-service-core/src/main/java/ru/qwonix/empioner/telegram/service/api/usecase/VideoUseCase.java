package ru.qwonix.empioner.telegram.service.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.entity.VideoDetails;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;
import ru.qwonix.empioner.telegram.service.api.VideoApi;
import ru.qwonix.empioner.telegram.service.mapper.VideoDtoMapper;
import ru.qwonix.empioner.telegram.service.model.CreateVideoRequest;
import ru.qwonix.empioner.telegram.service.spi.VideoSpi;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VideoUseCase implements VideoApi {

    private final VideoSpi videoDao;
    private final VideoDtoMapper mapper;

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
    public boolean updateTelegramFileIdByTelegramFileUniqueId(
            TelegramFileUniqueId telegramFileUniqueId,
            TelegramFileId telegramFileId) {
        videoDao.updateTelegramFileIdByTelegramFileUniqueId(telegramFileUniqueId, telegramFileId);
        return true;
    }

    @Override
    public VideoId create(CreateVideoRequest request, boolean needCreateNewVideoGroup) {
        VideoGroupId videoGroupId = videoDao.createVideoGroup();
        Video video = new Video(
                null,
                videoGroupId,
                "",
                request.telegramFileId(),
                request.telegramFileUniqueId(),
                1,
                false
        );
        VideoId videoId = videoDao.createVideo(videoGroupId, video);

        VideoDetails videoDetails = mapper.toVideoDetails(videoId, request);
        videoDao.createVideoDetails(videoDetails);

        return videoId;
    }
}
