package ru.qwonix.empioner.telegram.bot.service;

import ru.qwonix.empioner.telegram.bot.entity.Show;
import ru.qwonix.empioner.telegram.bot.entity.id.ShowId;

import java.util.List;
import java.util.Optional;

public interface ShowService {
    Optional<Show> findById(ShowId showId);

    List<Show> findAllOrderByNumberWithLimitAndPage(int limit, int page);
}
