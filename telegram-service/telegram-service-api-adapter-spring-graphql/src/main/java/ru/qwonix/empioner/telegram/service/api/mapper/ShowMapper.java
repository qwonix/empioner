package ru.qwonix.empioner.telegram.service.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.service.api.graphql.model.ShowInput;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShowMapper {
    ShowInput toInput(Show show);
}
