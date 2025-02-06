package ru.qwonix.empioner.service.entity.id;

import com.fasterxml.jackson.annotation.JsonValue;

public interface Id<T> {
    @JsonValue
    T value();
}
