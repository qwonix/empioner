package ru.qwonix.empioner.telegram.bot.spi;

import ru.qwonix.empioner.telegram.entity.Image;
import ru.qwonix.empioner.telegram.id.ImageId;

import java.util.Optional;

public interface ImageSpi {
    Optional<Image> findTelegramFileIdByImageId(ImageId id);
}
