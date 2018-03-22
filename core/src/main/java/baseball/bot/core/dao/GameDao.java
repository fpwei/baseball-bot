package baseball.bot.core.dao;

import baseball.bot.core.entity.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GameDao extends PagingAndSortingRepository<Game, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM game WHERE DATE(first_pitch_date)=?1")
    List<Game> findGamesByFirstPitchDateIs(String date);

}
