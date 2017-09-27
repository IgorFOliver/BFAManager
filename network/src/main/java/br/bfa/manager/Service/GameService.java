package br.bfa.manager.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.Game;
import br.bfa.manager.entity.Team;

/**
 * @author igor Furtado <igorfoliver@gmail.com>
 */
public interface GameService {

    List<Game> findAll();
    
    List<Game> findAllByOrderByDtGame();

    String delete(Long id);

	Game findById(Long id);

	ArrayList<Game> carregarXls(MultipartFile file, BindingResult bindingResult) throws InvalidFormatException, IOException;

	String saveAll(List<Game> games, Account creator);

	Game save(String path, MultipartFile file, Game game) throws IOException;

	List<Athlete> findAllByGameAndTeam(Game game, Team homeTeam);

	List<Game> findAllByTeamByOrderByDtGame(Team team);


}