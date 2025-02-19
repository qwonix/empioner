package ru.qwonix.empioner.telegram.bot.service;

import ru.qwonix.empioner.telegram.id.ShowId;

public interface BotSettingsService {
    boolean isEnabledSingleShowMode();
    ShowId getShowOfSingleShowMode();
}
