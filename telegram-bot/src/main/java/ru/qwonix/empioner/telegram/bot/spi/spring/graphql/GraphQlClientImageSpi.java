package ru.qwonix.empioner.telegram.bot.spi.spring.graphql;

import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.bot.spi.ImageSpi;
import ru.qwonix.empioner.telegram.entity.Image;
import ru.qwonix.empioner.telegram.id.ImageId;

import java.util.Optional;

@Repository
public class GraphQlClientImageSpi implements ImageSpi {

    private static final String GET_TELEGRAM_FILE_ID_QUERY = """
            query GetImageTelegramFileId($id: ImageId!) {
                getImageTelegramFileId(id: $id) {
                    id
                    telegramFileId
                    telegramFileUniqueId
                }
            }
            """;
    private final GraphQlClient graphQlClient;

    public GraphQlClientImageSpi(GraphQlClient graphQlClient) {
        this.graphQlClient = graphQlClient;
    }

    @Override
    public Optional<Image> findTelegramFileIdByImageId(ImageId id) {
        return Optional.ofNullable(
                graphQlClient.document(GET_TELEGRAM_FILE_ID_QUERY)
                        .variable("id", id.value())
                        .retrieve("getImageTelegramFileId")
                        .toEntity(Image.class)
                        .block()
        );
    }
}