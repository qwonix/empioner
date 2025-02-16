package ru.qwonix.empioner.telegram.bot.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.bot.entity.id.ShowId;
import ru.qwonix.empioner.telegram.bot.service.BotSettingsService;

import java.util.UUID;

@Service
public class BotSettingsServiceImpl implements BotSettingsService {

    @Value("${bot.single_show_mode.show_id:#{null}}")
    private UUID singleShowModeShowId;

    @Override
    public boolean isEnabledSingleShowMode() {
        return singleShowModeShowId != null;
    }

    @Override
    public ShowId getShowOfSingleShowMode() {
        return new ShowId(singleShowModeShowId);
    }
}
