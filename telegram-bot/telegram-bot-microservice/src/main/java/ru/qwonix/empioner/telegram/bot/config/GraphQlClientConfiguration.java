package ru.qwonix.empioner.telegram.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import io.netty.channel.ChannelOption;
import java.time.Duration;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GraphQlClientConfiguration {

    @Bean
    public GraphQlClient graphQlClient(WebClient webClient) {
        return HttpGraphQlClient.builder(webClient)
                .build();
    }

    @Bean
    public WebClient webClient(EmpionerServiceProperties empionerServiceProperties,
                               WebClient.Builder builder,
                               @Value("${empioner.service.username:empioner_bot}") String username,
                               @Value("${empioner.service.password:}") String password) {
        return builder
                .baseUrl(empionerServiceProperties.serviceUrl())
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                        .responseTimeout(Duration.ofSeconds(10))))
                .build();
    }

    @Bean
    public DgsGraphQlClient dgsGraphQlClient(GraphQlClient graphQlClient) {
        return DgsGraphQlClient.create(graphQlClient);
    }
}
