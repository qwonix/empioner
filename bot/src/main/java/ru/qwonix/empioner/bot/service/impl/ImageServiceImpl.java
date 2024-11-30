package ru.qwonix.empioner.bot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.bot.dao.ImageDao;
import ru.qwonix.empioner.bot.entity.Image;
import ru.qwonix.empioner.bot.entity.id.ImageId;
import ru.qwonix.empioner.bot.service.ImageService;

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
