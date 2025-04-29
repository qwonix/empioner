package ru.qwonix.empioner.telegram.service.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.qwonix.empioner.telegram.entity.Season;
import ru.qwonix.empioner.telegram.service.api.graphql.model.SeasonInput;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SeasonMapper {
    SeasonInput toInput(Season season);
}
