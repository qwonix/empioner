package ru.qwonix.empioner.telegram.bot.service;

import ru.qwonix.empioner.telegram.entity.Image;
import ru.qwonix.empioner.telegram.id.ImageId;

import java.util.Optional;

public interface ImageService {

    Optional<Image> findTelegramFileIdByImageId(ImageId id);
}
