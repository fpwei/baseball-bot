package baseball.bot.core.service;

import baseball.bot.core.entity.Game;

import java.util.List;

public interface GameService {

    List<Game> getGames(int year, int month, int date);

}
