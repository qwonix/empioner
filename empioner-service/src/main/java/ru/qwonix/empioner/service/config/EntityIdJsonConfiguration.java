package ru.qwonix.empioner.service.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import ru.qwonix.empioner.service.entity.id.*;

import java.io.IOException;
import java.util.UUID;

@JsonComponent
public class EntityIdJsonConfiguration {

    public static class EpisodeIdSerialize extends JsonSerializer<EpisodeId> {
        @Override
        public void serialize(EpisodeId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(String.valueOf(value.value()));
        }
    }

    public static class EpisodeIdDeserializer extends JsonDeserializer<EpisodeId> {
        @Override
        public EpisodeId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new EpisodeId(UUID.fromString(p.getText()));
        }
    }

    public static class MovieIdDeserializer extends JsonDeserializer<MovieId> {
        @Override
        public MovieId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new MovieId(UUID.fromString(p.getText()));
        }
    }

    public static class SeasonIdDeserializer extends JsonDeserializer<SeasonId> {
        @Override
        public SeasonId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new SeasonId(UUID.fromString(p.getText()));
        }
    }

    public static class SeriesIdDeserializer extends JsonDeserializer<SeriesId> {
        @Override
        public SeriesId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new SeriesId(UUID.fromString(p.getText()));
        }
    }

    public static class ShowIdDeserializer extends JsonDeserializer<ShowId> {
        @Override
        public ShowId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new ShowId(UUID.fromString(p.getText()));
        }
    }

    public static class VideoGroupIdDeserializer extends JsonDeserializer<VideoGroupId> {
        @Override
        public VideoGroupId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new VideoGroupId(UUID.fromString(p.getText()));
        }
    }

    public static class VideoIdDeserializer extends JsonDeserializer<VideoId> {
        @Override
        public VideoId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new VideoId(UUID.fromString(p.getText()));
        }
    }

    public static class TelegramBotUserIdDeserializer extends JsonDeserializer<TelegramBotUserId> {
        @Override
        public TelegramBotUserId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new TelegramBotUserId(Long.parseLong(p.getText()));
        }
    }

    public static class TelegramBotUserIdJsonSerializer extends JsonSerializer<TelegramBotUserId> {
        @Override
        public void serialize(TelegramBotUserId id, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeString(String.valueOf(id.value()));
        }
    }
}