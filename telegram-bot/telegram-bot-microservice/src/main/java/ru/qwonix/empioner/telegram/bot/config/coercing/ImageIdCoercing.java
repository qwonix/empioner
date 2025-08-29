package ru.qwonix.empioner.telegram.bot.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.bot.config.UUIDIdCoercing;
import ru.qwonix.empioner.telegram.id.ImageId;

import java.util.UUID;

@DgsScalar(name = "ImageId")
public class ImageIdCoercing extends UUIDIdCoercing<ImageId> {
    @Override
    public ImageId parseValue(UUID input) {
        return new ImageId(input);
    }
}
