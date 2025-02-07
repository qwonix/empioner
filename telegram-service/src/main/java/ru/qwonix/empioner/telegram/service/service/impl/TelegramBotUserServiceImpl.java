package ru.qwonix.empioner.telegram.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.service.dao.TelegramBotUserDao;
import ru.qwonix.empioner.telegram.service.dao.entity.TelegramBotUserDetails;
import ru.qwonix.empioner.telegram.service.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.service.entity.UserStatus;
import ru.qwonix.empioner.telegram.service.entity.id.TelegramBotUserId;
import ru.qwonix.empioner.telegram.service.service.TelegramBotUserService;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TelegramBotUserServiceImpl implements TelegramBotUserService {

    private final TelegramBotUserDao telegramBotUserDao;

    @Override
    public Optional<TelegramBotUser> findUser(TelegramBotUserId id) {
        return telegramBotUserDao.findUser(id);
    }

    public TelegramBotUser registerNewUser(TelegramBotUserDetails userDetails) {
        return telegramBotUserDao.registerNewUser(userDetails);
    }

    @Override
    public TelegramBotUser merge(TelegramBotUserDetails userDetails) {
        return this.findUser(new TelegramBotUserId(userDetails.id()))
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
}
