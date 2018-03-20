package baseball.bot.core.dao;

import baseball.bot.core.entity.Game;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameDao extends PagingAndSortingRepository<Game, Integer>{
}
