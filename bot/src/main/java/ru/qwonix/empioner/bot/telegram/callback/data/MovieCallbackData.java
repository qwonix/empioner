package ru.qwonix.empioner.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.bot.entity.id.MovieId;


public record MovieCallbackData(
        @JsonProperty("i") MovieId movieId
) implements CallbackData {
}