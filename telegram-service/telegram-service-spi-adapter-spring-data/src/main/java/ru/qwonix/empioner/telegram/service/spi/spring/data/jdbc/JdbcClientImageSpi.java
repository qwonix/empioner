package ru.qwonix.empioner.telegram.service.spi.spring.data.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.qwonix.empioner.telegram.entity.Image;
import ru.qwonix.empioner.telegram.id.ImageId;
import ru.qwonix.empioner.telegram.service.spi.ImageSpi;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcClientImageSpi implements ImageSpi {

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
