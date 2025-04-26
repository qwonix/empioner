package ru.qwonix.empioner.telegram.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.telegram.id.ShowId;


public record ShowSeriesCallbackData(
        @JsonProperty("i") ShowId showId,
        @JsonProperty("p") Integer page
) implements CallbackData {
}
