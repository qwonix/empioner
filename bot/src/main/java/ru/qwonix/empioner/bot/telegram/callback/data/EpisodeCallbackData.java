package ru.qwonix.empioner.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.qwonix.empioner.bot.entity.id.EpisodeId;

public record EpisodeCallbackData(
        @JsonProperty("i") EpisodeId episodeId
) implements CallbackData {
}