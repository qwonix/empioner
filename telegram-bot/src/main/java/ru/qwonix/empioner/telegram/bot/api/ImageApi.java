package ru.qwonix.empioner.telegram.bot.api;

import ru.qwonix.empioner.telegram.entity.Image;
import ru.qwonix.empioner.telegram.id.ImageId;

import java.util.Optional;

public interface ImageApi {

    Optional<Image> findTelegramFileIdByImageId(ImageId id);
}
