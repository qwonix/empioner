package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config;

import graphql.schema.CoercingParseValueException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import ru.qwonix.empioner.telegram.id.*;

import java.util.UUID;

@Configuration
public class GraphQlExtendedScalarTypesConfiguration {

    @Bean
    public RuntimeWiringConfigurer runtimeWiring() {
        return builder -> builder
                .scalar(GraphQLScalarType.newScalar().name("EpisodeId").coercing(new UUIDIdCoercing<>() {
                    @Override
                    public EpisodeId parseValue(UUID id) {
                        return new EpisodeId(id);
                    }
                }).build())
                .scalar(GraphQLScalarType.newScalar().name("SeasonId").coercing(new UUIDIdCoercing<>() {
                    @Override
                    public SeasonId parseValue(UUID id) {
                        return new SeasonId(id);
                    }
                }).build())
                .scalar(GraphQLScalarType.newScalar().name("SeriesId").coercing(new UUIDIdCoercing<>() {
                    @Override
                    public SeriesId parseValue(UUID id) {
                        return new SeriesId(id);
                    }
                }).build())
                .scalar(GraphQLScalarType.newScalar().name("MovieId").coercing(new UUIDIdCoercing<>() {
                    @Override
                    public MovieId parseValue(UUID id) {
                        return new MovieId(id);
                    }
                }).build())
                .scalar(GraphQLScalarType.newScalar().name("ShowId").coercing(new UUIDIdCoercing<>() {
                    @Override
                    public ShowId parseValue(UUID id) {
                        return new ShowId(id);
                    }
                }).build())
                .scalar(GraphQLScalarType.newScalar().name("VideoId").coercing(new UUIDIdCoercing<>() {
                    @Override
                    public VideoId parseValue(UUID id) {
                        return new VideoId(id);
                    }
                }).build())
                .scalar(GraphQLScalarType.newScalar().name("VideoGroupId").coercing(new UUIDIdCoercing<>() {
                    @Override
                    public VideoGroupId parseValue(UUID id) {
                        return new VideoGroupId(id);
                    }
                }).build())
                .scalar(GraphQLScalarType.newScalar().name("ImageId").coercing(new UUIDIdCoercing<>() {
                    @Override
                    public ImageId parseValue(UUID id) {
                        return new ImageId(id);
                    }
                }).build())
                .scalar(GraphQLScalarType.newScalar().name("TelegramBotUserId").coercing(new StringIdCoercing<>() {
                    @Override
                    public TelegramBotUserId parseValue(String id) throws CoercingParseValueException {
                        try {
                            return new TelegramBotUserId(Long.parseLong(id));
                        } catch (IllegalArgumentException e) {
                            throw new CoercingParseValueException(e.getLocalizedMessage(), e);
                        }
                    }
                }).build())
                .scalar(GraphQLScalarType.newScalar().name("TelegramFileId").coercing(new StringIdCoercing<>() {
                    @Override
                    public TelegramFileId parseValue(String id) throws CoercingParseValueException {
                            return new TelegramFileId(id);
                    }
                }).build())
                .scalar(GraphQLScalarType.newScalar().name("TelegramFileUniqueId").coercing(new StringIdCoercing<>() {
                    @Override
                    public TelegramFileUniqueId parseValue(String id) throws CoercingParseValueException {
                            return new TelegramFileUniqueId(id);
                    }
                }).build())
                .scalar(GraphQLScalarType.newScalar().name("TelegramFileUniqueId").coercing(new StringIdCoercing<>() {
                    @Override
                    public TelegramFileUniqueId parseValue(String id) throws CoercingParseValueException {
                            return new TelegramFileUniqueId(id);
                    }
                }).build())
                .build();
    }
}
