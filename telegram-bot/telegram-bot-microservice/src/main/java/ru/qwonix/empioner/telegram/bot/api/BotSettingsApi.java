package ru.qwonix.empioner.telegram.bot.api;

import ru.qwonix.empioner.telegram.id.ShowId;

public interface BotSettingsApi {
    boolean isEnabledSingleShowMode();

    ShowId getShowOfSingleShowMode();
}
