package ru.qwonix.empioner.telegram.bot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.bot.dao.MessageDao;
import ru.qwonix.empioner.telegram.entity.TelegramBotUser;
import ru.qwonix.empioner.telegram.bot.service.MessageService;
import ru.qwonix.empioner.telegram.bot.telegram.config.TelegramProperties;

@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageDao messageDao;
    private final TelegramProperties properties;

    @Override
    public void setMessageId(TelegramBotUser user, Integer messageId) {
        messageDao.put(user.id(), messageId, properties.messageIdTtl());
    }

    @Override
    public Integer getMessageId(TelegramBotUser user) {
        return messageDao.get(user.id());
    }

    @Override
    public boolean hasMessageId(TelegramBotUser user) {
        return messageDao.has(user.id());
    }

    @Override
    public void deleteMessageId(TelegramBotUser user) {
        messageDao.delete(user.id());
    }
}
