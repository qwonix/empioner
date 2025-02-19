package ru.qwonix.empioner.telegram.bot.dao.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.bot.dao.MessageDao;
import ru.qwonix.empioner.telegram.id.TelegramBotUserId;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class MessageDaoImpl implements MessageDao {

    private final RedisTemplate<Long, Integer> redisTemplate;
    private ValueOperations<Long, Integer> valueOperations;

    @PostConstruct
    private void init() {
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void put(TelegramBotUserId userId, Integer value, Duration timeout) {
        valueOperations.set(userId.value(), value, timeout);
    }

    @Override
    public Integer get(TelegramBotUserId userId) {
        return valueOperations.get(userId.value());
    }

    @Override
    public void delete(TelegramBotUserId userId) {
        redisTemplate.delete(userId.value());
    }

    @Override
    public Boolean has(TelegramBotUserId userId) {
        return redisTemplate.hasKey(userId.value());
    }
}