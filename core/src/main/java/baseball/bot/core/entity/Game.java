package baseball.bot.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int num;

    private int year;

    @OneToOne
    @JoinColumn(name = "away_id")
    private Team away;

    @OneToOne
    @JoinColumn(name = "home_id")
    private Team home;

}