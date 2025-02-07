package ru.qwonix.empioner.telegram.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.telegram.bot.entity.id.VideoId;

public record EpisodeVideoCallbackData(
        @JsonProperty("i") VideoId videoId
) implements CallbackData {
}
