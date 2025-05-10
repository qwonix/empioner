package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import ru.qwonix.empioner.telegram.entity.UserRole;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import ru.qwonix.empioner.telegram.service.api.TelegramBotUserApi;
import ru.qwonix.empioner.telegram.service.api.graphql.model.TelegramBotUserDetailsInput;
import ru.qwonix.empioner.telegram.service.api.graphql.model.TelegramBotUserInput;
import ru.qwonix.empioner.telegram.service.api.mapper.TelegramBotUserMapper;

import java.util.Set;

@DgsComponent
@RequiredArgsConstructor
public class TelegramBotUserGraphQLController {

    private final TelegramBotUserApi telegramBotUserApi;
    private final TelegramBotUserMapper mapper;

    @DgsQuery
    public Mono<TelegramBotUserInput> getTelegramBotUserById(@InputArgument TelegramBotUserId id) {
        return Mono.fromCallable(() -> telegramBotUserApi.findUser(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @DgsMutation
    public Mono<TelegramBotUserInput> mergeTelegramBotUser(@InputArgument TelegramBotUserDetailsInput userDetails) {
        return Mono.fromCallable(() -> telegramBotUserApi.merge(mapper.to(userDetails, Set.of(UserRole.ADMIN))))
                .map(mapper::toInput);
    }

    @DgsMutation
    public Mono<Boolean> addActivity(@InputArgument TelegramBotUserId id) {
        return Mono.fromRunnable(() -> telegramBotUserApi.addActivity(id));
    }

    @DgsMutation
    public Mono<Boolean> updateStatus(@InputArgument TelegramBotUserId id, @InputArgument UserStatus status) {
        return Mono.fromRunnable(() -> telegramBotUserApi.setStatus(id, status));
    }

    @DgsMutation
    public Mono<Boolean> makeAdmin(@InputArgument TelegramBotUserId id) {
        return Mono.fromRunnable(() -> telegramBotUserApi.makeAdmin(id));
    }
}
