package baseball.bot.crawler.task;

import baseball.bot.core.dao.PlayerDao;
import baseball.bot.core.dao.StartingPlayerDao;
import baseball.bot.core.entity.Game;
import baseball.bot.core.entity.StartingPlayer;
import baseball.bot.core.entity.Team;
import baseball.bot.core.service.GameService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class StartingPlayerTask {
    private static final String CPBL_BOX_URL = "http://www.cpbl.com.tw/games/box.html";

    private static final String PARAM_YEAR = "pbyear";
    private static final String PARAM_DATE = "game_date";
    private static final String PARAM_GAME_TYPE = "game_type";
    private static final String PARAM_GAME_NUM = "game_id";


    @Autowired
    private StartingPlayerDao startingPlayerDao;

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerDao playerDao;


    public void run() {

        Date today = new Date();
//        List<Game> games = gameService.getGames(today.getYear(),today.getMonth() + 1, today.getDate());
        List<Game> games = gameService.getGames(2018, 3, 19);

        for (Game game : games) {

            boolean awayStartingPlayerExists = startingPlayerDao.existsByGameIdAndTeamId(game.getId(), game.getAway().getId());
            boolean homeStartingPlayerExists = startingPlayerDao.existsByGameIdAndTeamId(game.getId(), game.getHome().getId());

            if (!awayStartingPlayerExists || !homeStartingPlayerExists) {
                Document document = null;
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(game.getFirstPitchDate());

                    document = Jsoup.connect(CPBL_BOX_URL)
                            .data(PARAM_YEAR, String.valueOf(game.getYear()),
                                    PARAM_GAME_NUM, String.valueOf(game.getNum()),
                                    PARAM_GAME_TYPE, game.getType(),
                                    PARAM_DATE, String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE)))
                            .get();


                    if (!awayStartingPlayerExists) {
                        getAwayStartingPlayer(document, game);
                    }

                    if (!homeStartingPlayerExists) {
                        getHomeStartingPlayer(document, game);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private void getAwayStartingPlayer(Document document, Game game) {
        Elements elements = document.getElementsByClass("half_block left");

        List<StartingPlayer> players = (getBetter(elements.get(0), game.getAway()));

        if (players.size() == 9) {
            StartingPlayer player = getPitcher(elements.get(1), game.getAway());
            if (player != null) {
                players.add(player);

                players.forEach(p -> p.setGame(game));
                startingPlayerDao.saveAll(players);
            }
        }
    }

    private void getHomeStartingPlayer(Document document, Game game) {
        Elements elements = document.getElementsByClass("half_block right");

        List<StartingPlayer> players = (getBetter(elements.get(0), game.getHome()));

        if (players.size() == 9) {
            StartingPlayer player = getPitcher(elements.get(1), game.getHome());
            if (player != null) {
                players.add(player);

                players.forEach(p -> p.setGame(game));
                startingPlayerDao.saveAll(players);
            }
        }
    }

    private List<StartingPlayer> getBetter(Element element, Team team) {
        List<Element> elements = element.getElementsByTag("tr").stream()
                .filter(e -> e.getElementsByTag("td").size() != 0 && e.selectFirst(".sub") == null && !e.hasClass("total")).collect(Collectors.toList());

        if (elements.isEmpty()) {
            return new ArrayList<>();
        }

        return IntStream.range(0, elements.size())
                .mapToObj(index -> {
                    StartingPlayer player = new StartingPlayer();
                    player.setNumber(index + 1);

                    String[] info = elements.get(index).child(0).text().split(",");
                    String playerName = info[0].trim();
                    player.setPlayer(playerDao.findByTeamIdAndName(team.getId(), playerName));

                    player.setTeam(team);

                    String position = info[1].trim();
                    if (position.contains(" ")) {
                        player.setPosition(position.substring(0, position.indexOf(" ")));
                    } else {
                        player.setPosition(position);
                    }

                    return player;
                }).collect(Collectors.toList());
    }

    private StartingPlayer getPitcher(Element element, Team team) {
        List<Element> elements = element.getElementsByTag("tr").stream()
                .filter(e -> e.getElementsByTag("td").size() != 0 && !e.hasClass("total")).collect(Collectors.toList());

        if (elements.isEmpty()) {
            return null;
        } else {
            Element e = elements.get(0);

            StartingPlayer player = new StartingPlayer();

            String playerName = e.child(0).text().trim();
            player.setPlayer(playerDao.findByTeamIdAndName(team.getId(), playerName));

            player.setTeam(team);

            player.setPosition("P");

            return player;
        }
    }


}
