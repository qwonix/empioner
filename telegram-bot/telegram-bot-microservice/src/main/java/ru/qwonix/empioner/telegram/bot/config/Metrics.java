package ru.qwonix.empioner.telegram.bot.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Metrics {
    TELEGRAM_UPDATES_TOTAL("telegram_updates_total");

    private final String name;
}
