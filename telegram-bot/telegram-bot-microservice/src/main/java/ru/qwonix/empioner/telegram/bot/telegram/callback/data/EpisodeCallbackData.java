package ru.qwonix.empioner.telegram.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.telegram.id.EpisodeId;

public record EpisodeCallbackData(
        @JsonProperty("i") EpisodeId episodeId
) implements CallbackData {
}