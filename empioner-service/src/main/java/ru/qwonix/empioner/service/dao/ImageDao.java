package ru.qwonix.empioner.service.dao;

import ru.qwonix.empioner.service.entity.Image;
import ru.qwonix.empioner.service.entity.id.ImageId;

import java.util.Optional;

public interface ImageDao {
    Optional<Image> findTelegramFileIdByImageId(ImageId id);
}
