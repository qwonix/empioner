package ru.qwonix.empioner.telegram.service.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.service.entity.Season;
import ru.qwonix.empioner.telegram.service.entity.id.ImageId;
import ru.qwonix.empioner.telegram.service.entity.id.SeasonId;
import ru.qwonix.empioner.telegram.service.entity.id.SeriesId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class SeasonMapper implements RowMapper<Season> {
    @Override
    public Season mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        String description = rs.getString("description");
        UUID seriesId = rs.getObject("series_id", UUID.class);
        int number = rs.getInt("number");
        UUID imageId = rs.getObject("preview_image_id", UUID.class);
        boolean isAvailable = rs.getBoolean("is_available");

        return new Season(new SeasonId(id),
                description,
                number,
                new SeriesId(seriesId),
                new ImageId(imageId),
                isAvailable);
    }
}
