package baseball.bot.core.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "starting_player")
public class StartingPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToOne
    @JoinColumn(name = "player_id")
    private Player player;

    private Integer number;

    private String position;

    @OneToOne
    @JoinColumn(name = "team_id")
    private Team team;

}
