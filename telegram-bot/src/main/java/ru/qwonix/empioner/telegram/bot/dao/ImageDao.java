package ru.qwonix.empioner.telegram.bot.dao;

import ru.qwonix.empioner.telegram.bot.entity.Image;
import ru.qwonix.empioner.telegram.bot.entity.id.ImageId;

import java.util.Optional;

public interface ImageDao {
    Optional<Image> findTelegramFileIdByImageId(ImageId id);
}
