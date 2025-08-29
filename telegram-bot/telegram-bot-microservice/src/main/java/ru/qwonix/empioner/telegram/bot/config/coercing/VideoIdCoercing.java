package ru.qwonix.empioner.telegram.bot.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.bot.config.UUIDIdCoercing;
import ru.qwonix.empioner.telegram.id.VideoId;

import java.util.UUID;

@DgsScalar(name = "VideoId")
public class VideoIdCoercing extends UUIDIdCoercing<VideoId> {
    @Override
    public VideoId parseValue(UUID input) {
        return new VideoId(input);
    }
}
