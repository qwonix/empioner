package ru.qwonix.empioner.telegram.service.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.service.dao.VideoDao;
import ru.qwonix.empioner.telegram.entity.Video;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class VideoDaoImpl implements VideoDao {

    private final JdbcClient jdbcClient;
    private final RowMapper<Video> mapper;

    @Override
    public Optional<Video> findMaxPriorityInGroup(VideoGroupId videoGroupId) {
        return jdbcClient.sql("""
                        select v.*
                        from video v
                                 join public.video_group vg on vg.id = v.video_group_id
                        where vg.id = :videoGroupId
                        order by priority DESC
                            fetch first 1 rows only""")
                .param("videoGroupId", videoGroupId.value())
                .query(mapper)
                .optional();
    }

    @Override
    public Optional<Video> findById(VideoId videoId) {
        return jdbcClient.sql("select * from video where id = :id")
                .param("id", videoId.value())
                .query(mapper)
                .optional();
    }

    @Override
    public List<Video> findAllByVideoGroupId(VideoGroupId videoGroupId) {
        return jdbcClient.sql("select * from video where video_group_id = :videoGroupId")
                .param("videoGroupId", videoGroupId.value())
                .query(mapper)
                .list();
    }
}
