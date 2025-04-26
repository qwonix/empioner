package ru.qwonix.empioner.telegram.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.telegram.id.MovieId;


public record MovieCallbackData(
        @JsonProperty("i") MovieId movieId
) implements CallbackData {
}