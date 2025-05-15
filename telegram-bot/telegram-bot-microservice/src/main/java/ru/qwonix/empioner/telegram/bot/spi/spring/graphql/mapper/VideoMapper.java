package ru.qwonix.empioner.telegram.bot.spi.spring.graphql.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.qwonix.empioner.telegram.service.api.graphql.model.AddVideoInput;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VideoMapper {
    AddVideoInput toAddVideoInput(ru.qwonix.empioner.telegram.bot.spi.spring.graphql.model.AddVideoInput addVideoInput);
}
