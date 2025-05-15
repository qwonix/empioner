package ru.qwonix.empioner.telegram.bot.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.bot.config.UUIDIdCoercing;
import ru.qwonix.empioner.telegram.id.SeasonId;

import java.util.UUID;

@DgsScalar(name = "SeasonId")
public class SeasonIdCoercing extends UUIDIdCoercing<SeasonId> {
    @Override
    public SeasonId parseValue(UUID input) {
        return new SeasonId(input);
    }
}
