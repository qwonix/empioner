package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config.UUIDIdCoercing;

import java.util.UUID;

@DgsScalar(name = "ShowId")
public class ShowIdCoercing extends UUIDIdCoercing<ShowId> {
    @Override
    public ShowId parseValue(UUID input) {
        return new ShowId(input);
    }
}
