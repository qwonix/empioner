package ru.qwonix.empioner.telegram.bot.telegram.callback.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "@")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeleteMessageCallbackData.class, name = "del"),
        @JsonSubTypes.Type(value = EmptyCallbackData.class, name = "emt"),
        @JsonSubTypes.Type(value = EpisodeCallbackData.class, name = "ep"),
        @JsonSubTypes.Type(value = EpisodeVideoCallbackData.class, name = "evid"),
        @JsonSubTypes.Type(value = MovieCallbackData.class, name = "mov"),
        @JsonSubTypes.Type(value = MovieVideoCallbackData.class, name = "mvid"),
        @JsonSubTypes.Type(value = SeasonCallbackData.class, name = "sea"),
        @JsonSubTypes.Type(value = SeriesCallbackData.class, name = "cer"),
        @JsonSubTypes.Type(value = ShowCallbackData.class, name = "sh"),
        @JsonSubTypes.Type(value = ShowMovieCallbackData.class, name = "shmov"),
        @JsonSubTypes.Type(value = ShowSeriesCallbackData.class, name = "shser"),
        @JsonSubTypes.Type(value = ShowShowCallbackData.class, name = "shsh")
})
public interface CallbackData {
}