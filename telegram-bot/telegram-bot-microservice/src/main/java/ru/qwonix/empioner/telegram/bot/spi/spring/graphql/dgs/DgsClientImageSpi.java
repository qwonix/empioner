package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.dgs;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.bot.config.coercing.ImageIdCoercing;
import ru.qwonix.empioner.telegram.bot.spi.ImageSpi;
import ru.qwonix.empioner.telegram.entity.Image;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.service.api.graphql.api.ImageByIdGraphQLQuery;
import ru.qwonix.empioner.telegram.service.api.graphql.api.ImageByIdProjectionRoot;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DgsClientImageSpi implements ImageSpi {

    private final DgsGraphQlClient dgsClient;

    private final ImageIdCoercing imageIdCoercing;

    @Override
    public Optional<Image> findTelegramFileIdByImageId(ImageId id) {
        return Optional.ofNullable(dgsClient.request(ImageByIdGraphQLQuery.newRequest()
                        .id(id)
                        .build())
                .coercing(ImageId.class, imageIdCoercing)
                .projection(new ImageByIdProjectionRoot<>()
                        .id()
                        .telegramFileId()
                        .telegramFileUniqueId())
                .retrieveSync()
                .toEntity(Image.class));
    }
}
