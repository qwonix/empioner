package ru.qwonix.empioner.bot.dao;

import ru.qwonix.empioner.bot.entity.Image;
import ru.qwonix.empioner.bot.entity.id.ImageId;

import java.util.Optional;

public interface ImageDao {
    Optional<Image> findTelegramFileIdByImageId(ImageId id);
}
