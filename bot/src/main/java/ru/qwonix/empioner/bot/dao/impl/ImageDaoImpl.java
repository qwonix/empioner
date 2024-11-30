package ru.qwonix.empioner.bot.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.bot.dao.ImageDao;
import ru.qwonix.empioner.bot.entity.Image;
import ru.qwonix.empioner.bot.entity.id.ImageId;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ImageDaoImpl implements ImageDao {

    private final JdbcClient jdbcClient;
    private final RowMapper<Image> mapper;

    @Override
    public Optional<Image> findTelegramFileIdByImageId(ImageId id) {
        return jdbcClient.sql("select * from image where id = :id")
                .param("id", id.value())
                .query(mapper)
                .optional();
    }
}
