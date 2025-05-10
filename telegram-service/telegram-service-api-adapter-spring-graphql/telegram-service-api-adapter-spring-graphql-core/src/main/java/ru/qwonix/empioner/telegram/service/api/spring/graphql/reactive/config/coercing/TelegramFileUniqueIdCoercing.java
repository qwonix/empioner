package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config.StringIdCoercing;

@DgsScalar(name = "TelegramFileUniqueId")
public class TelegramFileUniqueIdCoercing extends StringIdCoercing<TelegramFileUniqueId> {
    @Override
    public TelegramFileUniqueId parseValue(String input) {
        return new TelegramFileUniqueId(input);
    }
}
