package ru.qwonix.empioner.telegram.bot.config;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsRuntimeWiring;
import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;

@DgsComponent
public class LongScalarConfig {

    @DgsRuntimeWiring
    public RuntimeWiring.Builder registerLong(RuntimeWiring.Builder builder) {
        return builder.scalar(ExtendedScalars.GraphQLLong);
    }
}
