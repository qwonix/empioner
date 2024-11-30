package ru.qwonix.empioner.bot.dao.entity;

import java.time.Instant;

public record Activity(Long userId, Instant interactionDate) {
}