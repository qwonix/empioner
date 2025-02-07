package ru.qwonix.empioner.telegram.bot.entity.id;

import com.fasterxml.jackson.annotation.JsonValue;

public interface Id<T> {
    @JsonValue
    T value();
}
