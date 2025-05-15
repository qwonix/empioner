package ru.qwonix.empioner.telegram.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public WebClient webClient(EmpionerServiceProperties empionerServiceProperties) {
        return WebClient.builder()
                .baseUrl(empionerServiceProperties.serviceUrl())
                .build();
    }

    @Bean
    public DgsGraphQlClient dgsGraphQlClient(GraphQlClient graphQlClient) {
        return DgsGraphQlClient.create(graphQlClient);
    }
}
