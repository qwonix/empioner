package ru.qwonix.empioner.telegram.service.api.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.telegram.entity.UserStatus;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;
import ru.qwonix.empioner.telegram.service.api.TelegramBotUserApi;
import ru.qwonix.empioner.telegram.service.spi.TelegramBotUserSpi;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TelegramBotUserUseCase implements TelegramBotUserApi {

    private final TelegramBotUserSpi telegramBotUserDao;

    @Override
    public Optional<TelegramBotUser> findUser(TelegramBotUserId id) {
        return telegramBotUserDao.findUser(id);
    }

    public TelegramBotUser registerNewUser(TelegramBotUserDetails userDetails) {
        return telegramBotUserDao.registerNewUser(userDetails);
    }

    @Override
    public TelegramBotUser merge(TelegramBotUserDetails userDetails) {
        return this.findUser(userDetails.id())
                .orElseGet(() -> this.registerNewUser(userDetails));
    }

    @Override
    public void addActivity(TelegramBotUserId userId) {
        telegramBotUserDao.addActivity(userId);
    }

    @Override
    public void setStatus(TelegramBotUserId userId, UserStatus status) {
        telegramBotUserDao.updateStatus(userId, status);
    }

    @Override
    public void makeAdmin(TelegramBotUserId userId) {
        telegramBotUserDao.makeAdmin(userId);
    }
}
