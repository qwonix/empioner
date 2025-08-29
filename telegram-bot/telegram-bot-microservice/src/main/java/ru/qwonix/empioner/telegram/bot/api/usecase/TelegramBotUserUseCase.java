package ru.qwonix.empioner.telegram.bot.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.qwonix.empioner.telegram.bot.api.TelegramBotUserApi;
import ru.qwonix.empioner.telegram.bot.spi.TelegramBotUserSpi;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TelegramBotUserUseCase implements TelegramBotUserApi {

    private final TelegramBotUserSpi telegramBotUserSpi;

    @Override
    public Optional<TelegramBotUser> findUser(TelegramBotUserId id) {
        return telegramBotUserSpi.findUser(id);
    }

    public TelegramBotUser registerNewUser(User telegramUser) {
        return telegramBotUserSpi.registerNewUser(telegramUser);
    }

    @Override
    public TelegramBotUser merge(User telegramUser) {
        return this.findUser(new TelegramBotUserId(telegramUser.getId()))
                .orElseGet(() -> this.registerNewUser(telegramUser));
    }

    @Override
    public void addActivity(TelegramBotUserId userId) {
        telegramBotUserSpi.addActivity(userId);
    }

    @Override
    public void setStatus(TelegramBotUserId userId, UserStatus status) {
        telegramBotUserSpi.updateStatus(userId, status);
    }

    @Override
    public void makeAdmin(TelegramBotUserId id) {
        telegramBotUserSpi.makeAdmin(id);
    }
}
