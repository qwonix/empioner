package ru.qwonix.empioner.telegram.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.telegram.id.ShowId;

public record ShowCallbackData(
        @JsonProperty("i") ShowId showId
) implements CallbackData {
}
