package ru.qwonix.empioner.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.bot.entity.id.VideoId;

public record EpisodeVideoCallbackData(
        @JsonProperty("i") VideoId videoId
) implements CallbackData {
}
