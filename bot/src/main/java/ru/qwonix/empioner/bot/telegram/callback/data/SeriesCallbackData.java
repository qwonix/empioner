package ru.qwonix.empioner.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.bot.entity.id.SeriesId;

public record SeriesCallbackData(
        @JsonProperty("i") SeriesId seriesId,
        @JsonProperty("p") Integer page
) implements CallbackData {
}
