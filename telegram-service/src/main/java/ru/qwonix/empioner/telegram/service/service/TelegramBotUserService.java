package ru.qwonix.empioner.telegram.service.service;

import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.util.Optional;

public interface TelegramBotUserService {

    Optional<TelegramBotUser> findUser(TelegramBotUserId id);

    TelegramBotUser merge(TelegramBotUserDetails userDetails);

    void addActivity(TelegramBotUserId userId);

    void setStatus(TelegramBotUserId userId, UserStatus status);

}
