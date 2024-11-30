package ru.qwonix.empioner.bot.telegram.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

/**
 * @param token              Telegram bot API token
 * @param username           Telegram bot username
 * @param keyboardButtonsMax Maximum number of buttons under one message
 * @param messageIdTtl       TTL of message videoId that comes from Telegram
 */
@ConfigurationProperties("bot")
public record TelegramProperties(
        @DefaultValue("Player Bot")
        String title,
        @NotBlank(message = "API token cannot be unspecified")
        String token,
        @NotBlank(message = "Bot username cannot be unspecified")
        String username,
        String description,
        @DefaultValue("13")
        Integer keyboardButtonsMax,
        @DefaultValue("48h")
        Duration messageIdTtl
) {
}
