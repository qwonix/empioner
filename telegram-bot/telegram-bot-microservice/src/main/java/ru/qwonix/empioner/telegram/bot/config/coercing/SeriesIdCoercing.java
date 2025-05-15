package ru.qwonix.empioner.telegram.bot.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.bot.config.UUIDIdCoercing;

import java.util.UUID;

@DgsScalar(name = "SeriesId")
public class SeriesIdCoercing extends UUIDIdCoercing<SeriesId> {
    @Override
    public SeriesId parseValue(UUID input) {
        return new SeriesId(input);
    }
}
