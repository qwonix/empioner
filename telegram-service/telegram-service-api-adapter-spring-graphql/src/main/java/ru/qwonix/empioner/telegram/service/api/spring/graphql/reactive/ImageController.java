package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.service.api.ImageApi;
import ru.qwonix.empioner.telegram.service.api.graphql.api.ImageQueryResolver;
import ru.qwonix.empioner.telegram.service.api.graphql.model.ImageInput;
import ru.qwonix.empioner.telegram.service.api.mapper.ImageMapper;

@RequiredArgsConstructor
@Controller
public class ImageController implements ImageQueryResolver {

    private final ImageApi imageApi;
    private final ImageMapper mapper;

    @QueryMapping
    @Override
    public Mono<ImageInput> getImageTelegramFileId(@Argument ImageId id) {
        return Mono.fromCallable(() -> imageApi.findTelegramFileIdByImageId(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }
}