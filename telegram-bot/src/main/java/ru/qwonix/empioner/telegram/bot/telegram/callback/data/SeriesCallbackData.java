package ru.qwonix.empioner.telegram.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.telegram.id.SeriesId;

public record SeriesCallbackData(
        @JsonProperty("i") SeriesId seriesId,
        @JsonProperty("p") Integer page
) implements CallbackData {
}
