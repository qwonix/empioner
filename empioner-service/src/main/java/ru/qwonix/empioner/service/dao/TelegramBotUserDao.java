package ru.qwonix.empioner.service.dao;

import ru.qwonix.empioner.service.dao.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.service.entity.TelegramBotUser;
import ru.qwonix.empioner.service.entity.UserStatus;
import ru.qwonix.empioner.service.entity.id.TelegramBotUserId;

import java.util.Optional;

public interface TelegramBotUserDao {
    Optional<TelegramBotUser> findUser(TelegramBotUserId id);

    TelegramBotUser registerNewUser(TelegramBotUserDetails userDetails);

    void addActivity(TelegramBotUserId id);

    void updateStatus(TelegramBotUserId id, UserStatus status);
}
