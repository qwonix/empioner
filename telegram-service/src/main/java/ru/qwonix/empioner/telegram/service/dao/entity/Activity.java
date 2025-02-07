package ru.qwonix.empioner.telegram.service.dao.entity;

import java.time.Instant;

public record Activity(Long userId, Instant interactionDate) {
}