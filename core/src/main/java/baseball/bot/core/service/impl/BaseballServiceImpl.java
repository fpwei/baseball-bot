package baseball.bot.core.service.impl;

import baseball.bot.core.dao.StartingPlayerDao;
import baseball.bot.core.entity.Game;
import baseball.bot.core.entity.StartingPlayer;
import baseball.bot.core.service.BaseballService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BaseballServiceImpl implements BaseballService {

    private static final String STARTING_PLAYER_FOLDER = "starting";

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private StartingPlayerDao startingPlayerDao;

    @Value("${report.base.path:/data/baseball/report}")
    private String basePath;


    @Override
    public String getStartingPlayerUrl(Game game) {

        String filename = Paths.get(basePath, STARTING_PLAYER_FOLDER, getStartingPlayerFileName(game)).toString();

        File file = FileUtils.getFile(filename);

        if (file.exists()) {
            return filename;
        } else {
            try {
                return generateStartingPlayerReport(game, filename) == null ? null : filename;
            } catch (IOException | JRException e) {
                log.error(ExceptionUtils.getStackTrace(e));
                return null;
            }
        }

    }

    private File generateStartingPlayerReport(Game game, String filename) throws IOException, JRException {
        if (startingPlayerDao.existsByGameIdAndTeamId(game.getId(), game.getAway().getId()) &&
                startingPlayerDao.existsByGameIdAndTeamId(game.getId(), game.getHome().getId())) {

            Map<String, Object> params = new HashMap<>();

            List<StartingPlayer> awayStartingPlayers = startingPlayerDao.findByGameIdAndTeamId(game.getId(), game.getAway().getId());
            awayStartingPlayers.parallelStream().forEach(p -> {
                params.put("A" + p.getPosition(), p.getPlayer().getName());

                if (p.getNumber() != null) {
                    params.put("A" + p.getNumber(), p.getPlayer().getName());
                }
            });

            List<StartingPlayer> homeStartingPlayers = startingPlayerDao.findByGameIdAndTeamId(game.getId(), game.getHome().getId());
            homeStartingPlayers.parallelStream().forEach(p -> {
                params.put("H" + p.getPosition(), p.getPlayer().getName());

                if (p.getNumber() != null) {
                    params.put("H" + p.getNumber(), p.getPlayer().getName());
                }
            });

            JasperPrint jasperPrint = JasperFillManager.fillReport(getJasperReport("starting_player"), params, new JREmptyDataSource());
            BufferedImage image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, 0, 1);
            File imageFile = new File(filename);
            FileUtils.forceMkdir(imageFile.getParentFile());
            ImageIO.write(image, "jpg", imageFile);

            return imageFile;
        } else {
            throw new RuntimeException("Starting player data not exists");
        }
    }

    private JasperReport getJasperReport(String filename) throws IOException, JRException {
        String jrxmlFilePath = "jasperreport/jrxml/" + filename + ".jrxml";
        String jasperFilePath = "jasper/" + filename + ".jasper";
        Resource jasperResource = resourceLoader.getResource(jasperFilePath);

        if (jasperResource.exists()) {
            return (JasperReport) JRLoader.loadObject(jasperResource.getFile());
        } else {
            Resource jrxmlResource = resourceLoader.getResource(jrxmlFilePath);
            if (jrxmlResource.exists()) {
                //Compile .jrxml to .jasper
                File file = new File(jasperFilePath);
                FileUtils.forceMkdir(file.getParentFile());
                file.createNewFile();
                JasperCompileManager.compileReportToFile(jrxmlResource.getFile().getPath(), file.getPath());

                return (JasperReport) JRLoader.loadObjectFromFile(file.getPath());
            } else {
                throw new RuntimeException("Both compiled file and 'jasperreport/jrxml/" + filename + ".jrxml' are not exist");
            }
        }
    }


    private String getStartingPlayerFileName(Game game) {
        return String.format("starting-player-%d-%03d-%s.jpg", game.getYear(), game.getNum(), game.getType());
    }
}
