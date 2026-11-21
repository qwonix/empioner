package ru.qwonix.empioner.telegram.bot.api.usecase;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.qwonix.empioner.telegram.bot.api.TelegramBotUserApi;
import ru.qwonix.empioner.telegram.bot.spi.TelegramBotUserSpi;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.util.Optional;

@Service
public class TelegramBotUserUseCase implements TelegramBotUserApi {

    private final TelegramBotUserSpi telegramBotUserSpi;
    private final Counter registrations;

    public TelegramBotUserUseCase(TelegramBotUserSpi telegramBotUserSpi, MeterRegistry registry) {
        this.telegramBotUserSpi = telegramBotUserSpi;
        this.registrations = registry.counter("empioner.users.registered");
    }

    @Override
    public Optional<TelegramBotUser> findUser(TelegramBotUserId id) {
        return telegramBotUserSpi.findUser(id);
    }

    public TelegramBotUser registerNewUser(User telegramUser) {
        TelegramBotUser result = telegramBotUserSpi.registerNewUser(telegramUser);
        registrations.increment();
        return result;
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

}
