package ru.qwonix.empioner.telegram.bot.dao;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.qwonix.empioner.telegram.bot.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.bot.entity.UserStatus;
import ru.qwonix.empioner.telegram.bot.entity.id.TelegramBotUserId;

import java.util.Optional;

public interface TelegramBotUserDao {
    Optional<TelegramBotUser> findUser(TelegramBotUserId id);

    TelegramBotUser registerNewUser(User telegramUser);

    void addActivity(TelegramBotUserId id);

    void updateStatus(TelegramBotUserId id, UserStatus status);
}
