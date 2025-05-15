package ru.qwonix.empioner.telegram.bot.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.bot.config.UUIDIdCoercing;

import java.util.UUID;

@DgsScalar(name = "EpisodeId")
public class EpisodeIdCoercing extends UUIDIdCoercing<EpisodeId> {
    @Override
    public EpisodeId parseValue(UUID input) {
        return new EpisodeId(input);
    }
}
