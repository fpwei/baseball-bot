package baseball.bot.crawler.task;


import baseball.bot.core.entity.Game;
import baseball.bot.core.entity.Team;
import baseball.bot.core.service.TeamService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GameTask.class)
public class GameTaskTest {
    private static final Element element = Jsoup.parse("<div class=\"one_block\" style=\"display:block;\">\n" +
            "\t\t\n" +
            "\t<!-- 對戰球隊及場地 start -->\n" +
            "\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"schedule_team\">\n" +
            "\t\t<tbody><tr>\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t<td align=\"center\" valign=\"middle\" width=\"30\"><img src=\"http://cpbl-elta.cdn.hinet.net/phone/images/team/L01_logo_01.png\" width=\"30\" height=\"30\"></td>\t\t\t\n" +
            "\t\t\t<td align=\"center\" valign=\"middle\">新莊</td>\t\t\t\n" +
            "\t\t\t<td align=\"center\" valign=\"middle\" width=\"30\"><img src=\"http://cpbl-elta.cdn.hinet.net/phone/images/team/B04_logo_01.png\" width=\"30\" height=\"30\"></td>\t\t\t\n" +
            "\t\t\t\n" +
            "\t\t</tr>\n" +
            "\t</tbody></table>\n" +
            "\t<!-- 對戰球隊及場地 end -->\n" +
            "\t\n" +
            "\t\n" +
            "\t\n" +
            "\t<!-- 1:為延賽 -->\n" +
            "\t\t\t\n" +
            "\t\t\n" +
            "\t<!-- 一般賽事場次資訊(藍底) start -->\n" +
            "\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"schedule_info\">\n" +
            "\t\t<tbody><tr>\n" +
            "\t\t\t<th align=\"left\" valign=\"middle\" width=\"50\"></th>\n" +
            "\t\t\t<th align=\"center\" valign=\"middle\">12</th>\n" +
            "\t\t\t<th align=\"right\" valign=\"middle\" width=\"50\"></th>\n" +
            "\t\t</tr>\n" +
            "\t</tbody></table>\n" +
            "\t<!-- 一般賽事場次資訊(藍底) end -->\t\n" +
            "\t\n" +
            "\t\t\n" +
            "\t\n" +
            "\t\n" +
            "\t\n" +
            "\t\t\n" +
            "\t\n" +
            "\t<!-- 各種比數 start -->\n" +
            "\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"schedule_info\">\n" +
            "\t\t<!-- 比數:正常結束比賽 start -->\n" +
            "\t\t\t\n" +
            "\t</table>\n" +
            "\t\n" +
            "\t<!-- 各種比數 end -->\n" +
            "\t<!-- 未開打球賽與特殊賽事說明 start -->\n" +
            "\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"schedule_info\">\n" +
            "\t\t\t\t<tbody><tr>\n" +
            "\t\t\t<td align=\"center\" valign=\"middle\" width=\"30\">\n" +
            "\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
            "\t\t\t\t\t\t<td align=\"center\" valign=\"middle\">17:05</td>\n" +
            "\t\t\t <!-- 賽中 賽後時間不顯示 -->\n" +
            "\t\t\t\t\t\t<td align=\"center\" valign=\"middle\" width=\"30\">\n" +
            "            </td>\n" +
            "\t\t\t\t\t</tr>\n" +
            "\t\t\t\n" +
            "\t\t<tr>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t</tr>\n" +
            "\t</tbody></table>\n" +
            "\t<!-- 未開打球賽與特殊賽事說明 end -->\n" +
            "</div>");


    @InjectMocks
    private GameTask gameTask;

    @Mock
    private TeamService teamService;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(gameTask);
    }


    @Test
    public void testGetGameNum() throws Exception {
        int num = Whitebox.invokeMethod(gameTask, "getGameNum", element);

        Assert.assertEquals(12, num);
    }

//    @Test
//    public void testGetHome() throws Exception {
//
//
//        Team team = Whitebox.invokeMethod(gameTask, "getHome", element);
//
//
//
//        Assert.assertEquals(12, num);
//    }

}
