package ru.qwonix.empioner.telegram.bot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.qwonix.empioner.telegram.bot.dao.TelegramBotUserDao;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import ru.qwonix.empioner.telegram.bot.service.TelegramBotUserService;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TelegramBotUserServiceImpl implements TelegramBotUserService {

    private final TelegramBotUserDao telegramBotUserDao;

    @Override
    public Optional<TelegramBotUser> findUser(TelegramBotUserId id) {
        return telegramBotUserDao.findUser(id);
    }

    public TelegramBotUser registerNewUser(User telegramUser) {
        return telegramBotUserDao.registerNewUser(telegramUser);
    }

    @Override
    public TelegramBotUser merge(User telegramUser) {
        return this.findUser(new TelegramBotUserId(telegramUser.getId()))
                .orElseGet(() -> this.registerNewUser(telegramUser));
    }

    @Override
    public void addActivity(TelegramBotUserId userId) {
        telegramBotUserDao.addActivity(userId);
    }

    @Override
    public void setStatus(TelegramBotUserId userId, UserStatus status) {
        telegramBotUserDao.updateStatus(userId, status);
    }
}
