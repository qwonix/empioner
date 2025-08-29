package ru.qwonix.empioner.telegram.bot.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import ru.qwonix.empioner.telegram.bot.config.StringIdCoercing;
import ru.qwonix.empioner.telegram.id.TelegramFileId;

@DgsScalar(name = "TelegramFileId")
public class TelegramFileIdCoercing extends StringIdCoercing<TelegramFileId> {
    @Override
    public TelegramFileId parseValue(String input) {
        return new TelegramFileId(input);
    }
}
