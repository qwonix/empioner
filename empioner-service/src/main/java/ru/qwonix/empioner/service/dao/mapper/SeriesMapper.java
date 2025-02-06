package ru.qwonix.empioner.service.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.service.entity.Series;
import ru.qwonix.empioner.service.entity.id.ImageId;
import ru.qwonix.empioner.service.entity.id.SeriesId;
import ru.qwonix.empioner.service.entity.id.ShowId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class SeriesMapper implements RowMapper<Series> {
    @Override
    public Series mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        String title = rs.getString("title");
        String description = rs.getString("description");
        UUID showId = rs.getObject("show_id", UUID.class);
        UUID imageId = rs.getObject("preview_image_id", UUID.class);
        Integer priority = rs.getInt("priority");
        boolean isAvailable = rs.getBoolean("is_available");

        return new Series(new SeriesId(id),
                title,
                description,
                new ShowId(showId),
                new ImageId(imageId),
                priority,
                isAvailable);
    }
}
