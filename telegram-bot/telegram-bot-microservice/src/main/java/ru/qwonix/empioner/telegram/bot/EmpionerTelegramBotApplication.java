package ru.qwonix.empioner.telegram.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import io.micrometer.core.instrument.MeterRegistry;
import ru.qwonix.empioner.telegram.bot.config.ObservedTelegramClient;
import ru.qwonix.empioner.telegram.bot.config.EmpionerServiceProperties;
import ru.qwonix.empioner.telegram.bot.telegram.MoviePlayerBot;
import ru.qwonix.empioner.telegram.bot.telegram.config.TelegramProperties;

@ConfigurationPropertiesScan(basePackageClasses = {
        TelegramProperties.class, EmpionerServiceProperties.class
})
@SpringBootApplication
public class EmpionerTelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmpionerTelegramBotApplication.class, args);
    }

    @Bean
    public TelegramClient telegramClient(TelegramProperties telegramProperties, MeterRegistry registry) {
        return ObservedTelegramClient.wrap(new OkHttpTelegramClient(telegramProperties.token()), registry);
    }

    @Bean
    public SpringLongPollingBot springLongPollingBot(TelegramProperties telegramProperties, MoviePlayerBot moviePlayerBot) {
        return new SpringLongPollingBot() {
            @Override
            public String getBotToken() {
                return telegramProperties.token();
            }

            @Override
            public LongPollingUpdateConsumer getUpdatesConsumer() {
                return moviePlayerBot;
            }
        };
    }
}
