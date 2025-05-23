package ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.telegram.entity.Movie;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.id.MovieId;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class MovieRowMapper implements RowMapper<Movie> {
    @Override
    public Movie mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        String title = rs.getString("title");
        String description = rs.getString("description");
        UUID showId = rs.getObject("show_id", UUID.class);
        UUID imageId = rs.getObject("preview_image_id", UUID.class);
        UUID videoGroupId = rs.getObject("video_group_id", UUID.class);
        int priority = rs.getInt("priority");
        boolean isAvailable = rs.getBoolean("is_available");

        return new Movie(new MovieId(id),
                title,
                description,
                new ShowId(showId),
                new ImageId(imageId),
                new VideoGroupId(videoGroupId),
                priority,
                isAvailable);
    }
}
