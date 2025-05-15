package ru.qwonix.empioner.telegram.bot.config.coercing;

import com.netflix.graphql.dgs.DgsScalar;
import graphql.schema.CoercingParseValueException;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import ru.qwonix.empioner.telegram.bot.config.StringIdCoercing;

@DgsScalar(name = "TelegramBotUserId")
public class TelegramBotUserIdCoercing extends StringIdCoercing<TelegramBotUserId> {
    @Override
    public TelegramBotUserId parseValue(String input) {
        try {
            return new TelegramBotUserId(Long.parseLong(input));
        } catch (IllegalArgumentException e) {
            throw new CoercingParseValueException(e.getLocalizedMessage(), e);
        }
    }
}
