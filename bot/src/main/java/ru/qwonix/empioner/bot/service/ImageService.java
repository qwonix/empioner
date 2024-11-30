package ru.qwonix.empioner.bot.service;

import ru.qwonix.empioner.bot.entity.Image;
import ru.qwonix.empioner.bot.entity.id.ImageId;

import java.util.Optional;

public interface ImageService {

    Optional<Image> findTelegramFileIdByImageId(ImageId id);
}
