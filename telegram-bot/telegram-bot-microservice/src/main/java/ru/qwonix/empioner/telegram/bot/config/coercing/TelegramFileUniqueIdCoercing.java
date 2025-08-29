package ru.qwonix.empioner.telegram.bot.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.bot.config.StringIdCoercing;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;

@DgsScalar(name = "TelegramFileUniqueId")
public class TelegramFileUniqueIdCoercing extends StringIdCoercing<TelegramFileUniqueId> {
    @Override
    public TelegramFileUniqueId parseValue(String input) {
        return new TelegramFileUniqueId(input);
    }
}
