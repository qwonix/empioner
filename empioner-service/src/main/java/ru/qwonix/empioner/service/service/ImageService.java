package ru.qwonix.empioner.service.service;

import ru.qwonix.empioner.service.entity.Image;
import ru.qwonix.empioner.service.entity.id.ImageId;

import java.util.Optional;

public interface ImageService {

    Optional<Image> findTelegramFileIdByImageId(ImageId id);
}
