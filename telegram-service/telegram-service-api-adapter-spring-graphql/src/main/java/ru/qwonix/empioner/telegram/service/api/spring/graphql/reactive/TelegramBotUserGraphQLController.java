package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import ru.qwonix.empioner.telegram.service.api.TelegramBotUserApi;
import ru.qwonix.empioner.telegram.service.api.graphql.api.UserMutationResolver;
import ru.qwonix.empioner.telegram.service.api.graphql.api.UserQueryResolver;
import ru.qwonix.empioner.telegram.service.api.graphql.model.TelegramBotUserDetailsInput;
import ru.qwonix.empioner.telegram.service.api.graphql.model.TelegramBotUserInput;
import ru.qwonix.empioner.telegram.service.api.mapper.TelegramBotUserMapper;

@Controller
@RequiredArgsConstructor
public class TelegramBotUserGraphQLController implements UserQueryResolver, UserMutationResolver {

    private final TelegramBotUserApi telegramBotUserApi;
    private final TelegramBotUserMapper mapper;

    @QueryMapping
    @Override
    public Mono<TelegramBotUserInput> getTelegramBotUserById(@Argument TelegramBotUserId id) {
        return Mono.fromCallable(() -> telegramBotUserApi.findUser(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @MutationMapping
    @Override
    public Mono<TelegramBotUserInput> mergeTelegramBotUser(@Argument TelegramBotUserDetailsInput userDetails) {
        return Mono.fromCallable(() -> telegramBotUserApi.merge(mapper.to(userDetails)))
                .map(mapper::toInput);
    }

    @MutationMapping
    @Override
    public Mono<Boolean> addActivity(@Argument TelegramBotUserId id) {
        return Mono.fromRunnable(() -> telegramBotUserApi.addActivity(id));
    }

    @MutationMapping
    @Override
    public Mono<Boolean> updateStatus(@Argument TelegramBotUserId id, @Argument UserStatus status) {
        return Mono.fromRunnable(() -> telegramBotUserApi.setStatus(id, status));
    }

    @MutationMapping
    public Mono<Boolean> makeAdmin(@Argument TelegramBotUserId id) {
        return Mono.fromRunnable(() -> telegramBotUserApi.makeAdmin(id));
    }
}
