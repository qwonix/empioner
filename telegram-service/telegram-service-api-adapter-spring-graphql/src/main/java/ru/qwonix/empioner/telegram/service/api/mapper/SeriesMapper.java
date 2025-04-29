package ru.qwonix.empioner.telegram.service.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.service.api.graphql.model.SeriesInput;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SeriesMapper {
    SeriesInput toInput(Series series);
}
