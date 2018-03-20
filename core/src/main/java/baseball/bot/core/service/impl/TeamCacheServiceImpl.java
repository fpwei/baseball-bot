package baseball.bot.core.service.impl;

import baseball.bot.core.dao.TeamDao;
import baseball.bot.core.entity.Team;
import baseball.bot.core.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TeamCacheServiceImpl implements TeamService{

    @Autowired
    private TeamDao teamDao;

    private Map<String, Team> teamMap = new HashMap<>();

    @Override
    public Team getTeamByName(String name) {

        if(!teamMap.containsKey(name)){
            synchronized (this) {
                if(!teamMap.containsKey(name)){
                    Team team = teamDao.getTeamByName(name);
                    if (team == null) {
                        team = new Team();
                        team.setName(name);
                    }

                    teamMap.put(name, team);
                }
            }
        }

        return teamMap.get(name);
    }
}
