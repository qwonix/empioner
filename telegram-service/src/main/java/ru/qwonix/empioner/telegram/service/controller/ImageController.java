package ru.qwonix.empioner.telegram.service.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.entity.Image;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.service.service.ImageService;

@Controller
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @QueryMapping
    public Mono<Image> getImageTelegramFileId(@Argument ImageId id) {
        return Mono.fromCallable(() -> imageService.findTelegramFileIdByImageId(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }
}