package baseball.bot.crawler;

import baseball.bot.core.entity.StartingPlayer;
import baseball.bot.crawler.task.GameTask;
import baseball.bot.crawler.task.PlayerTask;
import baseball.bot.crawler.task.StartingPlayerTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Crawler {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Crawler.class, args);
//        ctx.getBean(GameTask.class).run();
        ctx.getBean(StartingPlayerTask.class).run();
    }

}
