package ru.qwonix.empioner.telegram.id;

import com.fasterxml.jackson.annotation.JsonValue;

public interface Id<T> {
    @JsonValue
    T value();
}
