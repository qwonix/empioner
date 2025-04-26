package ru.qwonix.empioner.telegram.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ShowShowCallbackData(
        @JsonProperty("p") Integer page
) implements CallbackData {
}
