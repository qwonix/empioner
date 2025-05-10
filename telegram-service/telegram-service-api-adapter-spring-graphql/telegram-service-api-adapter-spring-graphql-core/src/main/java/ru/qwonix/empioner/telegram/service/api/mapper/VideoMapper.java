package ru.qwonix.empioner.telegram.service.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.service.api.graphql.model.AddVideoInput;
import ru.qwonix.empioner.telegram.service.api.graphql.model.VideoInput;
import ru.qwonix.empioner.telegram.service.model.CreateVideoRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VideoMapper {
    VideoInput toInput(Video video);

    CreateVideoRequest toRequest(AddVideoInput video);
}
