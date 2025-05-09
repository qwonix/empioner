package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config.StringIdCoercing;

@DgsScalar(name = "TelegramFileId")
public class TelegramFileIdCoercing extends StringIdCoercing<TelegramFileId> {
    @Override
    public TelegramFileId parseValue(String input) {
        return new TelegramFileId(input);
    }
}
