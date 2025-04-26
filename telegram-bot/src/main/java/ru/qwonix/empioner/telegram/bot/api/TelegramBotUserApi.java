package ru.qwonix.empioner.telegram.bot.api;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.util.Optional;

public interface TelegramBotUserApi {

    Optional<TelegramBotUser> findUser(TelegramBotUserId id);

    TelegramBotUser merge(User telegramUser);

    void addActivity(TelegramBotUserId userId);

    void setStatus(TelegramBotUserId userId, UserStatus status);

    void makeAdmin(TelegramBotUserId id);
}
