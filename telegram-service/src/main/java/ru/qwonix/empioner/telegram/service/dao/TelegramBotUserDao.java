package ru.qwonix.empioner.telegram.service.dao;

import ru.qwonix.empioner.telegram.service.dao.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.telegram.service.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.service.entity.UserStatus;
import ru.qwonix.empioner.telegram.service.entity.id.TelegramBotUserId;

import java.util.Optional;

public interface TelegramBotUserDao {
    Optional<TelegramBotUser> findUser(TelegramBotUserId id);

    TelegramBotUser registerNewUser(TelegramBotUserDetails userDetails);

    void addActivity(TelegramBotUserId id);

    void updateStatus(TelegramBotUserId id, UserStatus status);
}
