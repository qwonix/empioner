package ru.qwonix.empioner.bot.entity.id;

import com.fasterxml.jackson.annotation.JsonValue;

public interface Id<T> {
    @JsonValue
    T value();
}
