package ru.qwonix.empioner.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ShowShowCallbackData(
        @JsonProperty("p") Integer page
) implements CallbackData {
}
