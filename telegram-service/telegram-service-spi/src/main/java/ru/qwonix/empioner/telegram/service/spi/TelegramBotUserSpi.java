package ru.qwonix.empioner.telegram.service.spi;

import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.util.Optional;

public interface TelegramBotUserSpi {
    Optional<TelegramBotUser> findUser(TelegramBotUserId id);

    TelegramBotUser registerNewUser(TelegramBotUserDetails userDetails);

    void addActivity(TelegramBotUserId id);

    void updateStatus(TelegramBotUserId id, UserStatus status);

    void makeAdmin(TelegramBotUserId userId);
}
