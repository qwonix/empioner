package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import ru.qwonix.empioner.telegram.service.api.TelegramBotUserApi;

@Controller
@RequiredArgsConstructor
public class TelegramBotUserGraphQLController {

    private final TelegramBotUserApi telegramBotUserApi;

    @QueryMapping
    public Mono<TelegramBotUser> getTelegramBotUserById(@Argument TelegramBotUserId id) {
        return Mono.fromCallable(() -> telegramBotUserApi.findUser(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @MutationMapping
    public Mono<TelegramBotUser> mergeTelegramBotUser(@Argument TelegramBotUserDetails userDetails) {
        return Mono.fromCallable(() -> telegramBotUserApi.merge(userDetails));
    }

    @MutationMapping
    public Mono<Void> addActivity(@Argument TelegramBotUserId id) {
        return Mono.fromRunnable(() -> telegramBotUserApi.addActivity(id));
    }

    @MutationMapping
    public Mono<Void> updateStatus(@Argument TelegramBotUserId id, @Argument UserStatus status) {
        return Mono.fromRunnable(() -> telegramBotUserApi.setStatus(id, status));
    }
}
