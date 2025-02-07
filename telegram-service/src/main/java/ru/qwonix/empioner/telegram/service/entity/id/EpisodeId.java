package ru.qwonix.empioner.telegram.service.entity.id;

import java.util.UUID;

public record EpisodeId(UUID value) implements Id<UUID> {
}
