package baseball.bot.core.dao;

import baseball.bot.core.entity.StartingPlayer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StartingPlayerDao extends PagingAndSortingRepository<StartingPlayer, Integer>{
    List<StartingPlayer> getStartingPlayersByGameIdAndTeamIdOrderByNumber(int gameId, int teamId);
}
