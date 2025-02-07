package ru.qwonix.empioner.telegram.service.service;

import ru.qwonix.empioner.telegram.service.dao.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.telegram.service.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.service.entity.UserStatus;
import ru.qwonix.empioner.telegram.service.entity.id.TelegramBotUserId;

import java.util.Optional;

public interface TelegramBotUserService {

    Optional<TelegramBotUser> findUser(TelegramBotUserId id);

    TelegramBotUser merge(TelegramBotUserDetails userDetails);

    void addActivity(TelegramBotUserId userId);

    void setStatus(TelegramBotUserId userId, UserStatus status);

}
