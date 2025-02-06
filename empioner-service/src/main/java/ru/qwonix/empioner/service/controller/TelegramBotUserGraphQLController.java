package ru.qwonix.empioner.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.service.dao.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.service.entity.TelegramBotUser;
import ru.qwonix.empioner.service.entity.UserStatus;
import ru.qwonix.empioner.service.entity.id.TelegramBotUserId;
import ru.qwonix.empioner.service.service.TelegramBotUserService;

@Controller
@RequiredArgsConstructor
public class TelegramBotUserGraphQLController {

    private final TelegramBotUserService telegramBotUserService;

    @QueryMapping
    public Mono<TelegramBotUser> getTelegramBotUserById(@Argument TelegramBotUserId id) {
        return Mono.fromCallable(() -> telegramBotUserService.findUser(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @MutationMapping
    public Mono<TelegramBotUser> mergeTelegramBotUser(@Argument TelegramBotUserDetails userDetails) {
        return Mono.fromCallable(() -> telegramBotUserService.merge(userDetails));
    }

    @MutationMapping
    public Mono<Void> addActivity(@Argument TelegramBotUserId id) {
        return Mono.fromRunnable(() -> telegramBotUserService.addActivity(id));
    }

    @MutationMapping
    public Mono<Void> updateStatus(@Argument TelegramBotUserId id, @Argument UserStatus status) {
        return Mono.fromRunnable(() -> telegramBotUserService.setStatus(id, status));
    }
}
