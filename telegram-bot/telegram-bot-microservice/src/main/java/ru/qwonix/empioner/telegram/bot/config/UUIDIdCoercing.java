package ru.qwonix.empioner.telegram.bot.config;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingSerializeException;
import org.jetbrains.annotations.NotNull;
import ru.qwonix.empioner.telegram.id.Id;

import java.util.Locale;
import java.util.UUID;

public abstract class UUIDIdCoercing<T> implements Coercing<T, String> {
    @Override
    public String serialize(Object dataFetcherResult, GraphQLContext graphQLContext, Locale locale) {
        if (dataFetcherResult == null) return null;
        if (dataFetcherResult instanceof Id<?> id) {
            return String.valueOf(id.value());
        }
        throw new CoercingSerializeException("Unable to serialize " + dataFetcherResult + " as ID");
    }

    @Override
    public T parseValue(Object input, GraphQLContext graphQLContext, Locale locale) {
        if (input == null) return null;
        if (input instanceof String id) {
            try {
                UUID uuid = UUID.fromString(id);
                return this.parseValue(uuid);
            } catch (IllegalArgumentException e) {
                throw new CoercingSerializeException("Unable to parse " + input + " to ID", e);
            }
        }
        throw new CoercingSerializeException("Unable to parse " + input + " to ID");
    }

    @Override
    public @NotNull Value<?> valueToLiteral(@NotNull Object input, @NotNull GraphQLContext context, @NotNull Locale locale) {
        if (input instanceof Id<?> id) {
            String uuidString = id.value().toString();
            return StringValue.newStringValue(uuidString).build();
        } else {
            throw new IllegalArgumentException("Expected a SeasonId object.");
        }
    }

    @Override
    public T parseLiteral(Value input, CoercedVariables variables, GraphQLContext graphQLContext, Locale locale) {
        if (input instanceof StringValue id) {
            return this.parseValue(id.getValue(), graphQLContext, locale);
        }
        throw new CoercingSerializeException("Unable to parse " + input + " to ID");
    }

    public abstract T parseValue(UUID input);
}