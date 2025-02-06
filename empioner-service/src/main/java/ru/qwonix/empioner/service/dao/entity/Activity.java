package ru.qwonix.empioner.service.dao.entity;

import java.time.Instant;

public record Activity(Long userId, Instant interactionDate) {
}