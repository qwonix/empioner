package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import ru.qwonix.empioner.telegram.id.*;

import java.util.UUID;

@Configuration
public class ConverterConfig {

    @Bean
    public Converter<String, EpisodeId> episodeIdConverter() {
        return new Converter<>() {
            @Override
            public EpisodeId convert(String source) {
                return new EpisodeId(UUID.fromString(source));
            }
        };
    }

    @Bean
    public Converter<String, ImageId> imageIdConverter() {
        return new Converter<>() {
            @Override
            public ImageId convert(String source) {
                return new ImageId(UUID.fromString(source));
            }
        };
    }

    @Bean
    public Converter<String, MovieId> movieIdConverter() {
        return new Converter<>() {
            @Override
            public MovieId convert(String source) {
                return new MovieId(UUID.fromString(source));
            }
        };
    }

    @Bean
    public Converter<String, SeriesId> seriesIdConverter() {
        return new Converter<>() {
            @Override
            public SeriesId convert(String source) {
                return new SeriesId(UUID.fromString(source));
            }
        };
    }

    @Bean
    public Converter<String, ShowId> showIdConverter() {
        return new Converter<>() {
            @Override
            public ShowId convert(String source) {
                return new ShowId(UUID.fromString(source));
            }
        };
    }

    @Bean
    public Converter<String, VideoGroupId> videoGroupIdConverter() {
        return new Converter<>() {
            @Override
            public VideoGroupId convert(String source) {
                return new VideoGroupId(UUID.fromString(source));
            }
        };
    }

    @Bean
    public Converter<String, VideoId> videoIdConverter() {
        return new Converter<>() {
            @Override
            public VideoId convert(String source) {
                return new VideoId(UUID.fromString(source));
            }
        };
    }
}
