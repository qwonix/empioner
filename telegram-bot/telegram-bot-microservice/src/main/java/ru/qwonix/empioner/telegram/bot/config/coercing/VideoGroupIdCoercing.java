package ru.qwonix.empioner.telegram.bot.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.bot.config.UUIDIdCoercing;
import ru.qwonix.empioner.telegram.id.VideoGroupId;

import java.util.UUID;

@DgsScalar(name = "VideoGroupId")
public class VideoGroupIdCoercing extends UUIDIdCoercing<VideoGroupId> {
    @Override
    public VideoGroupId parseValue(UUID input) {
        return new VideoGroupId(input);
    }
}
