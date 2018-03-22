package baseball.bot.crawler.task;

import baseball.bot.core.dao.PlayerDao;
import baseball.bot.core.dao.TeamDao;
import baseball.bot.core.entity.Player;
import baseball.bot.core.entity.Team;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlayerTask {
    private static final String PLAYER_LIST_URL = "http://www.cpbl.com.tw/web/team_player.php";

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private PlayerDao playerDao;

    public void run() {
        List<Player> players = new ArrayList<>();

        for (Team team : teamDao.findAll()) {
            String code = getTeamCode(team);

            Document document = null;
            try {
                document = Jsoup.connect(PLAYER_LIST_URL).data("team", code).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Element element = document.selectFirst("table.std_tb");
            Elements trPlayers = element.getElementsByTag("tr");


            players.addAll(trPlayers.parallelStream()
                    .filter(tr -> tr.getElementsByTag("td").size() != 0)
                    .map(tr -> {

                        Player player = new Player();

                        player.setNumber(Integer.valueOf(tr.child(0).text()));
                        player.setTeam(team);
                        player.setName(tr.child(1).text());

                        return player;
                    }).collect(Collectors.toList()));

        }
        playerDao.saveAll(players);

    }

    private String getTeamCode(Team team) {
        switch (team.getName()) {
            case "富邦悍將":
                return "B04";
            case "Lamigo桃猿":
                return "A02";
            case "統一7-ELEVEN獅":
                return "L01";
            case "中信兄弟":
                return "E02";
            default:
                return null;
        }
    }

}
