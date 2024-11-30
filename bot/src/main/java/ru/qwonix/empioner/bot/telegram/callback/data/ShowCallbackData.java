package ru.qwonix.empioner.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.bot.entity.id.ShowId;

public record ShowCallbackData(
        @JsonProperty("i") ShowId showId
) implements CallbackData {
}
