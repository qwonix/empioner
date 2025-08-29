package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.dgs;

import com.netflix.graphql.dgs.client.codegen.BaseProjectionNode;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.DgsGraphQlClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.qwonix.empioner.telegram.bot.config.coercing.TelegramBotUserIdCoercing;
import ru.qwonix.empioner.telegram.bot.spi.TelegramBotUserSpi;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import ru.qwonix.empioner.telegram.service.api.graphql.api.*;
import ru.qwonix.empioner.telegram.service.api.graphql.model.TelegramBotUserDetailsInput;

import java.util.Collections;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class DgsClientTelegramBotUserSpi implements TelegramBotUserSpi {

    private final DgsGraphQlClient dgsClient;

    private final TelegramBotUserIdCoercing telegramBotUserIdCoercing;

    @Override
    public Optional<TelegramBotUser> findUser(TelegramBotUserId id) {
        return Optional.ofNullable(dgsClient.request(TelegramBotUserByIdGraphQLQuery.newRequest()
                        .id(id)
                        .build())
                .coercing(TelegramBotUserId.class, telegramBotUserIdCoercing)
                .projection(new TelegramBotUserByIdProjectionRoot<>()
                        .id())
                .retrieveSync()
                .toEntity(TelegramBotUser.class));
    }

    @Override
    public TelegramBotUser registerNewUser(User telegramUser) {
        return dgsClient.request(MergeTelegramBotUserGraphQLQuery.newRequest()
                        .userDetails(TelegramBotUserDetailsInput.newBuilder()
                                .id(new TelegramBotUserId(telegramUser.getId()))
                                .firstName(telegramUser.getFirstName())
                                .lastName(telegramUser.getLastName())
                                .username(telegramUser.getUserName())
                                .languageCode(telegramUser.getLanguageCode())
                                .roles(Collections.emptyList())
                                .status(ru.qwonix.empioner.telegram.service.api.graphql.model.UserStatus.MEMBER)
                                .build())
                        .build())
                .coercing(TelegramBotUserId.class, telegramBotUserIdCoercing)
                .projection(new MergeTelegramBotUserProjectionRoot<>()
                        .id())
                .retrieveSync()
                .toEntity(TelegramBotUser.class);
    }

    @Override
    public void addActivity(TelegramBotUserId id) {
        dgsClient.request(AddActivityGraphQLQuery.newRequest()
                        .id(id)
                        .build())
                .coercing(TelegramBotUserId.class, telegramBotUserIdCoercing)
                .projection(new BaseProjectionNode() {
                })
                .retrieveSync();
    }

    @Override
    public void updateStatus(TelegramBotUserId id, UserStatus status) {
        dgsClient.request(UpdateStatusGraphQLQuery.newRequest()
                        .id(id)
                        .status(ru.qwonix.empioner.telegram.service.api.graphql.model.UserStatus.MEMBER)
                        .build())
                .coercing(TelegramBotUserId.class, telegramBotUserIdCoercing)
                .projection(new BaseProjectionNode() {
                })
                .retrieveSync();
    }

    @Override
    public void makeAdmin(TelegramBotUserId id) {
        dgsClient.request(MakeAdminGraphQLQuery.newRequest()
                        .id(id)
                        .build())
                .coercing(TelegramBotUserId.class, telegramBotUserIdCoercing)
                .projection(new BaseProjectionNode() {
                })
                .retrieveSync();
    }
}
