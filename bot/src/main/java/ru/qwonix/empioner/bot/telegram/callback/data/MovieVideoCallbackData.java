package ru.qwonix.empioner.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.bot.entity.id.VideoId;

public record MovieVideoCallbackData(
        @JsonProperty("i") VideoId videoId
) implements CallbackData {
}
