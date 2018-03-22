package baseball.bot.crawler.task;

import baseball.bot.core.dao.GameDao;
import baseball.bot.core.entity.Game;
import baseball.bot.core.entity.Team;
import baseball.bot.core.service.TeamService;
import baseball.bot.crawler.config.AppConfig;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GameTask {
    private static final String SCHEDULE_URL = "http://www.cpbl.com.tw/schedule/index";

    private static final String PARAM_DATE = "date";
    private static final String PARAM_GAME_TYPE = "sgameno";

    private static final String REGULAR = "01";

    @Autowired
    private TeamService teamService;

    @Autowired
    private GameDao gameDao;

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        ctx.getBean(GameTask.class).run();
    }

    public void run() {
        List<Game> games = new ArrayList<>();
        int year = 2018;
        for (int i = 1; i <= 12; i++) {
            try {
//                Document document = Jsoup.connect("http://www.cpbl.com.tw/schedule/index?date=2018-1-01&sgameno=01").get();

                final int month = i;
                Document document = Jsoup.connect(SCHEDULE_URL)
                        .data(PARAM_DATE, String.format("%d-%02d-01", year, month), PARAM_GAME_TYPE, REGULAR)
                        .get();

                Map<Integer, Element> dateMap = parseSchedule(document);


                for (int day : dateMap.keySet()) {
                    Elements elements = dateMap.get(day).getElementsByClass("one_block");

                    games.addAll(elements.parallelStream()
                            .map(e -> {
                                Game game = new Game();
                                game.setAway(getAway(e));
                                game.setHome(getHome(e));
                                game.setVenue(getVenue(e));
                                game.setNum(getGameNum(e));
                                game.setFirstPitchDate(getFirstPitchDate(e, year, month, day));
                                game.setYear(year);
                                game.setType(REGULAR);
                                return game;
                            }).collect(Collectors.toList()));


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        gameDao.saveAll(games);
    }

    private Map<Integer, Element> parseSchedule(Document document) {

        Map<Integer, Element> dateMap = new HashMap<>();

        Elements elements = document.getElementsByClass("schedule");
        Element schedule;
        if ((schedule = elements.first()) != null) {
            Elements trElements = schedule.select(".schedule > tbody > tr:not(.day)");

            for (int day = 0, game = 1; day < trElements.size() && game < trElements.size(); day += 2, game += 2) {
                Elements days = trElements.get(day).children();
                Elements games = trElements.get(game).children();

                for (int i = 0; i < days.size(); i++) {
                    if (StringUtils.isNumeric(days.get(i).text()) && StringUtils.isNotBlank(games.get(i).text())) {
                        dateMap.put(Integer.valueOf(days.get(i).text()), games.get(i));
                    }
                }
            }

        } else {
            throw new RuntimeException("parse schedule failed");
        }

        return dateMap;
    }

    private Date getFirstPitchDate(Element e, int year, int month, int day) {
        Elements elements = e.getElementsByClass("schedule_info");

        if (elements != null && elements.size() >= 3) {
            Element element = elements.get(2);
            element = element.selectFirst("td:nth-of-type(2)");

            if (element != null && StringUtils.isNotBlank(element.text())) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day, Integer.valueOf(element.text().split(":")[0]), Integer.valueOf(element.text().split(":")[1]), 0);
                return calendar.getTime();
            } else {
                throw new RuntimeException("parse first pitch date failed");
            }

        } else {
            throw new RuntimeException("parse first pitch date failed");
        }

    }


    private Team getAway(Element e) {
        Element element = e.selectFirst(".schedule_team td:nth-of-type(1)");
        Element img = element.selectFirst("img");

        if (img != null) {
            return parseTeamImgUrl(img.attr("src"));
        }

        return null;
    }

    private Team getHome(Element e) {
        Element element = e.selectFirst(".schedule_team td:nth-of-type(3)");
        Element img = element.selectFirst("img");

        if (img != null) {
            return parseTeamImgUrl(img.attr("src"));
        }

        return null;
    }

    private String getVenue(Element e) {
        Element element = e.selectFirst(".schedule_team td:nth-of-type(2)");

        return element.text();
    }

    private int getGameNum(Element e) {
        Element element = e.selectFirst(".schedule_info th:nth-of-type(2)");

        return Integer.valueOf(element.text());
    }

    private Team parseTeamImgUrl(String url) {

        String img = url.substring(url.lastIndexOf("/") + 1);

        String teamName;

        if (img.startsWith("A")) {
            teamName = "Lamigo桃猿";
        } else if (img.startsWith("B")) {
            teamName = "富邦悍將";
        } else if (img.startsWith("E")) {
            teamName = "中信兄弟";
        } else if (img.startsWith("L")) {
            teamName = "統一7-ELEVEN獅";
        } else {
            throw new IllegalArgumentException();
        }

        return teamService.getTeamByName(teamName);
    }

}
