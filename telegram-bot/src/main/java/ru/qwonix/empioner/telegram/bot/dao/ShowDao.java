package ru.qwonix.empioner.telegram.bot.dao;

import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.id.ShowId;

import java.util.List;
import java.util.Optional;

public interface ShowDao {
    Optional<Show> findById(ShowId showId);

    List<Show> findAllOrderByNumberWithLimitAndPage(int limit, int page);
}
