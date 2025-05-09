package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.id.SeasonId;
import ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config.UUIDIdCoercing;

import java.util.UUID;

@DgsScalar(name = "SeasonId")
public class SeasonIdCoercing extends UUIDIdCoercing<SeasonId> {
    @Override
    public SeasonId parseValue(UUID input) {
        return new SeasonId(input);
    }
}
