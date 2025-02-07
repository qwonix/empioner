package ru.qwonix.empioner.telegram.bot.service;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.qwonix.empioner.telegram.bot.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.bot.entity.UserStatus;
import ru.qwonix.empioner.telegram.bot.entity.id.TelegramBotUserId;

import java.util.Optional;

public interface TelegramBotUserService {

    Optional<TelegramBotUser> findUser(TelegramBotUserId id);

    TelegramBotUser merge(User telegramUser);

    void addActivity(TelegramBotUserId userId);

    void setStatus(TelegramBotUserId userId, UserStatus status);
}
