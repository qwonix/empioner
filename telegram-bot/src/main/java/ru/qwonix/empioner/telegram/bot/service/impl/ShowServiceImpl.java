package ru.qwonix.empioner.telegram.bot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.qwonix.empioner.telegram.bot.dao.ShowDao;
import ru.qwonix.empioner.telegram.entity.Show;
import ru.qwonix.empioner.telegram.id.ShowId;
import ru.qwonix.empioner.telegram.bot.service.ShowService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ShowServiceImpl implements ShowService {

    private final ShowDao showDao;

    @Override
    public Optional<Show> findById(ShowId showId) {
        return showDao.findById(showId);
    }

    @Override
    public List<Show> findAllOrderByNumberWithLimitAndPage(int limit, int page) {
        return showDao.findAllOrderByNumberWithLimitAndPage(limit, page);
    }
}
