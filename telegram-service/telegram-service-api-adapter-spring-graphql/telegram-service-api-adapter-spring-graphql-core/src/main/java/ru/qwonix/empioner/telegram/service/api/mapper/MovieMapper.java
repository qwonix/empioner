package ru.qwonix.empioner.telegram.service.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.qwonix.empioner.telegram.entity.Movie;
import ru.qwonix.empioner.telegram.service.api.graphql.model.MovieInput;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovieMapper {
    MovieInput toInput(Movie movie);
}
