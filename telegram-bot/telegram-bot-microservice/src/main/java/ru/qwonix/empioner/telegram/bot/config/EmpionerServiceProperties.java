package ru.qwonix.empioner.telegram.bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("empioner")
public record EmpionerServiceProperties(
        @DefaultValue("http://localhost:8080/graphql")
        String serviceUrl
) {
}
