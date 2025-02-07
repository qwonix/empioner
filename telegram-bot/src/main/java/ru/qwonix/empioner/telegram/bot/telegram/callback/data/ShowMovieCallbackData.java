package ru.qwonix.empioner.telegram.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.telegram.bot.entity.id.ShowId;


public record ShowMovieCallbackData(
        @JsonProperty("i") ShowId showId,
        @JsonProperty("p") Integer page
) implements CallbackData {
}
