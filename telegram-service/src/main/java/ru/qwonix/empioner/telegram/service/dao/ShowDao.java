package ru.qwonix.empioner.telegram.service.dao;

import ru.qwonix.empioner.telegram.service.entity.Show;
import ru.qwonix.empioner.telegram.service.entity.id.ShowId;

import java.util.List;
import java.util.Optional;

public interface ShowDao {
    Optional<Show> findById(ShowId showId);

    List<Show> findAllOrderByNumberWithLimitAndPage(int limit, int page);
}
