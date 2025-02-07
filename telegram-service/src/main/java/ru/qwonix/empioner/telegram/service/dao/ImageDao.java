package ru.qwonix.empioner.telegram.service.dao;

import ru.qwonix.empioner.telegram.service.entity.Image;
import ru.qwonix.empioner.telegram.service.entity.id.ImageId;

import java.util.Optional;

public interface ImageDao {
    Optional<Image> findTelegramFileIdByImageId(ImageId id);
}
