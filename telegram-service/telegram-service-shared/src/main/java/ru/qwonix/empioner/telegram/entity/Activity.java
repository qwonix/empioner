package ru.qwonix.empioner.telegram.entity;

import java.time.Instant;

public record Activity(Long userId, Instant interactionDate) {
}