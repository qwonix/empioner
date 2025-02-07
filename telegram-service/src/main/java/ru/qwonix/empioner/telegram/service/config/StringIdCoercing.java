package ru.qwonix.empioner.telegram.service.config;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import ru.qwonix.empioner.telegram.service.entity.id.Id;

import java.util.Locale;

abstract class StringIdCoercing<T> implements Coercing<T, String> {
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
                return this.parseValue(id);
            } catch (IllegalArgumentException e) {
                throw new CoercingSerializeException("Unable to parse " + input + " to ID", e);
            }
        }
        throw new CoercingSerializeException("Unable to parse " + input + " to ID");
    }

    @Override
    public T parseLiteral(Value input, CoercedVariables variables, GraphQLContext graphQLContext, Locale locale) {
        if (input instanceof StringValue id) {
            return this.parseValue(id.getValue(), graphQLContext, locale);
        }
        throw new CoercingSerializeException("Unable to parse " + input + " to ID");
    }

    public abstract T parseValue(String input) throws CoercingParseValueException;
}