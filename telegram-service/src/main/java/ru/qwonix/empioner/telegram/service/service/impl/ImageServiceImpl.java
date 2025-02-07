package ru.qwonix.empioner.telegram.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.service.dao.ImageDao;
import ru.qwonix.empioner.telegram.service.entity.Image;
import ru.qwonix.empioner.telegram.service.entity.id.ImageId;
import ru.qwonix.empioner.telegram.service.service.ImageService;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageDao imageDao;

    @Override
    public Optional<Image> findTelegramFileIdByImageId(ImageId id) {
        return imageDao.findTelegramFileIdByImageId(id);
    }
}
