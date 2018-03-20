package baseball.bot.crawler;

import baseball.bot.crawler.task.GameTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Crawler {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Crawler.class, args);
        ctx.getBean(GameTask.class).run();

    }

}
