package ru.qwonix.empioner.telegram.bot.service;

import ru.qwonix.empioner.telegram.bot.entity.Image;
import ru.qwonix.empioner.telegram.bot.entity.id.ImageId;

import java.util.Optional;

public interface ImageService {

    Optional<Image> findTelegramFileIdByImageId(ImageId id);
}
