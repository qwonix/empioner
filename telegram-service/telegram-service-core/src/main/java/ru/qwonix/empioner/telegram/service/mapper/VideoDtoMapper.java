package ru.qwonix.empioner.telegram.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.qwonix.empioner.telegram.entity.VideoDetails;
import ru.qwonix.empioner.telegram.id.VideoId;
import ru.qwonix.empioner.telegram.service.model.CreateVideoRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VideoDtoMapper {
    VideoDetails toVideoDetails(VideoId videoId, CreateVideoRequest video);
}
