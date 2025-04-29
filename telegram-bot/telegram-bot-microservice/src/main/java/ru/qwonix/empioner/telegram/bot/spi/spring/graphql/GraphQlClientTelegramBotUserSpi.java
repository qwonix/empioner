package ru.qwonix.empioner.telegram.bot.spi.spring.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.qwonix.empioner.telegram.bot.spi.TelegramBotUserSpi;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class GraphQlClientTelegramBotUserSpi implements TelegramBotUserSpi {

    private static final String GET_BY_ID_QUERY = """
            query GetTelegramBotUserById($id: TelegramBotUserId!) {
                getTelegramBotUserById(id: $id) {
                    id
                }
            }
            """;
    private static final String MERGE_USER_MUTATION = """
            mutation MergeTelegramBotUser($userDetails: TelegramBotUserDetailsInput!) {
                mergeTelegramBotUser(userDetails: $userDetails) {
                    id
                }
            }
            """;
    private static final String ADD_ACTIVITY_MUTATION = """
            mutation AddActivity($id: TelegramBotUserId!) {
                addActivity(id: $id)
            }
            """;
    private static final String UPDATE_STATUS_MUTATION = """
            mutation UpdateStatus($id: TelegramBotUserId!, $status: UserStatus!) {
                updateStatus(id: $id, status: $status)
            }
            """;

    private static final String MAKE_ADMIN_MUTATION = """
            mutation MakeAdmin($id: TelegramBotUserId!) {
                makeAdmin(id: $id)
            }
            """;

    private final GraphQlClient graphQlClient;

    @Override
    public Optional<TelegramBotUser> findUser(TelegramBotUserId id) {
        return graphQlClient.document(GET_BY_ID_QUERY)
                .variable("id", id.value().toString())
                .retrieve("getTelegramBotUserById")
                .toEntity(TelegramBotUser.class)
                .blockOptional();
    }

    @Override
    public TelegramBotUser registerNewUser(User telegramUser) {
        return graphQlClient.document(MERGE_USER_MUTATION)
                .variable("userDetails", Map.of(
                        "id", String.valueOf(telegramUser.getId()),
                        "firstName", String.valueOf(telegramUser.getFirstName()),
                        "lastName", String.valueOf(telegramUser.getLastName()),
                        "username", String.valueOf(telegramUser.getUserName()),
                        "languageCode", String.valueOf(telegramUser.getLanguageCode()),
                        "roles", Collections.emptySet(),
                        "status", String.valueOf(UserStatus.MEMBER)
                ))
                .retrieve("mergeTelegramBotUser")
                .toEntity(TelegramBotUser.class)
                .block();
    }

    @Override
    public void addActivity(TelegramBotUserId id) {
        graphQlClient.document(ADD_ACTIVITY_MUTATION)
                .variable("id", id.value().toString())
                .retrieve("addActivity")
                .toEntity(Boolean.class)
                .block();
    }

    @Override
    public void updateStatus(TelegramBotUserId id, UserStatus status) {
        graphQlClient.document(UPDATE_STATUS_MUTATION)
                .variables(Map.of(
                        "id", id.value().toString(),
                        "status", status.name()
                ))
                .retrieve("updateStatus")
                .toEntity(Boolean.class)
                .block();
    }

    @Override
    public void makeAdmin(TelegramBotUserId id) {
        graphQlClient.document(MAKE_ADMIN_MUTATION)
                .variables(Map.of(
                        "id", id.value().toString()
                ))
                .retrieve("makeAdmin")
                .toEntity(Boolean.class)
                .block();
    }
}