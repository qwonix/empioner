package ru.qwonix.empioner.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.service.dao.VideoDao;
import ru.qwonix.empioner.service.entity.Video;
import ru.qwonix.empioner.service.entity.id.VideoGroupId;
import ru.qwonix.empioner.service.entity.id.VideoId;
import ru.qwonix.empioner.service.service.VideoService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VideoServiceImpl implements VideoService {

    private final VideoDao videoDao;

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
}
