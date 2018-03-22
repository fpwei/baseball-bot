package baseball.bot.core.dao;

import baseball.bot.core.entity.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamDao extends CrudRepository<Team, Integer> {
    Team getTeamByName(String name);
}
