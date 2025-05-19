package ru.qwonix.empioner.telegram.service.spi.spring.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfiguration {

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new TelegramBotUserIdToLongConverter());
        converters.add(new LongToTelegramBotUserIdConverter());
        return new MongoCustomConversions(converters);
    }
}

class TelegramBotUserIdToLongConverter implements Converter<TelegramBotUserId, Long> {
    @Override
    public Long convert(TelegramBotUserId source) {
        return source.value();
    }
}

class LongToTelegramBotUserIdConverter implements Converter<Long, TelegramBotUserId> {
    @Override
    public TelegramBotUserId convert(Long source) {
        return new TelegramBotUserId(source);
    }
}