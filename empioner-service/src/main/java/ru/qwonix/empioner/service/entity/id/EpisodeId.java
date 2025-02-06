package ru.qwonix.empioner.service.entity.id;

import java.util.UUID;

public record EpisodeId(UUID value) implements Id<UUID> {
}
