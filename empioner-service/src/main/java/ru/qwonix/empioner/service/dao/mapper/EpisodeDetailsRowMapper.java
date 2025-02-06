package ru.qwonix.empioner.service.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.qwonix.empioner.service.entity.EpisodeDetails;
import ru.qwonix.empioner.service.entity.id.EpisodeId;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class EpisodeDetailsRowMapper implements RowMapper<EpisodeDetails> {

    @Override
    public EpisodeDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID episode_id = rs.getObject("episode_id", UUID.class);
        int productionCode = rs.getInt("production_code");

        return new EpisodeDetails(new EpisodeId(episode_id), productionCode);
    }
}

