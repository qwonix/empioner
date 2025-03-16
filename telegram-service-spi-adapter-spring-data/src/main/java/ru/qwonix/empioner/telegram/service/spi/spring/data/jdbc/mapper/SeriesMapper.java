package ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.entity.Series;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.id.SeriesId;
import ru.qwonix.empioner.telegram.id.ShowId;

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
