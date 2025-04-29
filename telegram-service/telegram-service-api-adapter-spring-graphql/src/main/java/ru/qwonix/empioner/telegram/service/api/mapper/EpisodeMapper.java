package ru.qwonix.empioner.telegram.service.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.qwonix.empioner.telegram.entity.Episode;
import ru.qwonix.empioner.telegram.service.api.graphql.model.EpisodeInput;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EpisodeMapper {
    EpisodeInput toInput(Episode episode);
}
