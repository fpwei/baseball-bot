package baseball.bot.core.service.impl;

import baseball.bot.core.dao.GameDao;
import baseball.bot.core.entity.Game;
import baseball.bot.core.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class GameServiceImpl implements GameService{
    @Autowired
    private GameDao gameDao;

    @Override
    public List<Game> getGames(int year, int month, int date) {

        return gameDao.findGamesByFirstPitchDateIs(String.format("%d-%d-%d", year, month, date));
    }
}
