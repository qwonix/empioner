package ru.qwonix.empioner.telegram.service.service;

import ru.qwonix.empioner.telegram.service.entity.Image;
import ru.qwonix.empioner.telegram.service.entity.id.ImageId;

import java.util.Optional;

public interface ImageService {

    Optional<Image> findTelegramFileIdByImageId(ImageId id);
}
