package ru.qwonix.empioner.telegram.service.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.service.api.graphql.model.AddVideoInput;
import ru.qwonix.empioner.telegram.service.api.graphql.model.VideoInput;
import ru.qwonix.empioner.telegram.service.model.CreateVideoRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VideoMapper {
    @Mapping(target = "description", source = "description", qualifiedByName = "qualityLabel")
    VideoInput toInput(Video video);

    CreateVideoRequest toRequest(AddVideoInput video);

    @Named("qualityLabel")
    default String qualityLabel(String label) {
        if (label == null) return null;
        return switch (label.trim().replace(" ", "").toLowerCase(java.util.Locale.ROOT)) {
            case "fullhd", "1080p" -> "Full HD";
            case "sd", "480p" -> "SD";
            default -> label;
        };
    }
}
