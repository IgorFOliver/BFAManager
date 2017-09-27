package br.bfa.manager.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Team;

/**
 * @author igor Furtado <igorfoliver@gmail.com>
 */
public interface TeamService {

    /**
     * Retrieves all Teams from database
     *
     * @return
     */
    List<Team> findAllByStatus(Boolean status);

    /**
     * Adds a new Team to the database
     * @param path 
     * @param file 
     *
     * @param Team
     * @return
     */
    String save(String path, MultipartFile file, Team Team);

    /**
     * Deletes a Team by Id from database
     *
     * @param id
     * @return
     */
    String delete(Long id);

	List<Team> findAllByStatusOrderByName(Boolean true1);

	Team findById(Long id);

	Team findByEmail(String username);

	ArrayList<Team> carregarXls(MultipartFile file) throws InvalidFormatException, IOException;

	String saveAll(List<Team> teams, Account creator);

	List<Team> findAllByAuthorities(Account account);

	Team findByAthletesEmailIn(String email);

	List<Team> findAllTeamsWithImportsAtive();

}