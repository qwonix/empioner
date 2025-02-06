package ru.qwonix.empioner.service.service;

import ru.qwonix.empioner.service.dao.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.service.entity.TelegramBotUser;
import ru.qwonix.empioner.service.entity.UserStatus;
import ru.qwonix.empioner.service.entity.id.TelegramBotUserId;

import java.util.Optional;

public interface TelegramBotUserService {

    Optional<TelegramBotUser> findUser(TelegramBotUserId id);

    TelegramBotUser merge(TelegramBotUserDetails userDetails);

    void addActivity(TelegramBotUserId userId);

    void setStatus(TelegramBotUserId userId, UserStatus status);

}
