package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.service.api.ImageApi;
import ru.qwonix.empioner.telegram.service.api.graphql.model.ImageInput;
import ru.qwonix.empioner.telegram.service.api.mapper.ImageMapper;

@RequiredArgsConstructor
@DgsComponent
public class ImageController {

    private final ImageApi imageApi;
    private final ImageMapper mapper;

    @DgsQuery
    public Mono<ImageInput> getImageTelegramFileId(@InputArgument ImageId id) {
        return Mono.fromCallable(() -> imageApi.findTelegramFileIdByImageId(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }
}