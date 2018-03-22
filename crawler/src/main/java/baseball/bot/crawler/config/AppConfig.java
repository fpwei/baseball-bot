package baseball.bot.crawler.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "baseball.bot.core.dao")
@EntityScan(basePackages = "baseball.bot.core.entity")
@ComponentScan(basePackages = "baseball.bot")
public class AppConfig {
}
