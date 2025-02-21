package ru.qwonix.empioner.telegram.bot.api.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.bot.api.BotSettingsApi;

import java.util.UUID;

@Service
public class BotSettingsUseCase implements BotSettingsApi {

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
