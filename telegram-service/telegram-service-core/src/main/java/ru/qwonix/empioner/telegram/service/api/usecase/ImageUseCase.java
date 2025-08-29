package ru.qwonix.empioner.telegram.service.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.entity.Image;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.service.api.ImageApi;
import ru.qwonix.empioner.telegram.service.spi.ImageSpi;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ImageUseCase implements ImageApi {

    private final ImageSpi imageDao;

    @Override
    public Optional<Image> findTelegramFileIdByImageId(ImageId id) {
        return imageDao.findTelegramFileIdByImageId(id);
    }
}
