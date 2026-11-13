package ru.qwonix.empioner.telegram.service.model;

import ru.qwonix.empioner.telegram.id.SeasonId;

public record CreateEpisodeRequest(SeasonId seasonId, String title, String description, int number) {
    public CreateEpisodeRequest {
        if (seasonId == null || seasonId.value() == null) {
            throw new IllegalArgumentException("Season ID is required");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Episode title must not be blank");
        }
        if (description == null) {
            throw new IllegalArgumentException("Episode description is required");
        }
        if (number < 1) {
            throw new IllegalArgumentException("Episode number must be positive");
        }
    }
}
