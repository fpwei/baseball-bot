package baseball.bot.core.dao;

import baseball.bot.core.entity.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerDao extends CrudRepository<Player, Integer>{
    Player findByTeamIdAndName(int teamId, String name);
}
