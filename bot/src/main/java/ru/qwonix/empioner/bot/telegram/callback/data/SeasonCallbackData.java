package ru.qwonix.empioner.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.bot.entity.id.SeasonId;

public record SeasonCallbackData(
        @JsonProperty("i") SeasonId seasonId,
        @JsonProperty("p") Integer page
) implements CallbackData {
}
