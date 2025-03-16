package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.entity.Image;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.service.api.ImageApi;

@RequiredArgsConstructor
@Controller
public class ImageController {

    private final ImageApi imageApi;

    @QueryMapping
    public Mono<Image> getImageTelegramFileId(@Argument ImageId id) {
        return Mono.fromCallable(() -> imageApi.findTelegramFileIdByImageId(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }
}