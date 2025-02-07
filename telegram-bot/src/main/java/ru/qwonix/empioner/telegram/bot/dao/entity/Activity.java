package ru.qwonix.empioner.telegram.bot.dao.entity;

import java.time.Instant;

public record Activity(Long userId, Instant interactionDate) {
}