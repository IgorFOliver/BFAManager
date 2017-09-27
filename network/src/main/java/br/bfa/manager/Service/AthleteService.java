package br.bfa.manager.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.Team;

/**
 * @author igor Furtado <igorfoliver@gmail.com>
 */
public interface AthleteService {

    /**
     * Retrieves all Athletes from database
     *
     * @return
     */
    List<Athlete> findAllByStatus(Boolean status);

    /**
     * Adds a new Athlete to the database
     * @param path 
     * @param file 
     *
     * @param Athlete
     * @return
     */
    String save(String path, MultipartFile file, Athlete Athlete);

    /**
     * Deletes a Athlete by Id from database
     *
     * @param id
     * @return
     */
    String delete(Long id);

	List<Athlete> findAllByStatusAndTeamOrderByName(Boolean status, Team team);

	Athlete findById(Long id);

	Athlete findByEmail(String username);

	ArrayList<Athlete> carregarXls(MultipartFile file, BindingResult bindingResult) throws InvalidFormatException, IOException;

	String saveAll(List<Athlete> athletes, Account creator, Team team);

	Athlete findByLegalId(String legalId);

	Athlete findByLegalId2(String legalId2);

	Athlete save(Athlete athlete);

	Integer countByContractSignedTrue();

	Integer countByStatusTrue();

	Integer countByConference(Long i);

	
	List<Athlete> findAllAthletesAtiveSigned();

	List<Athlete> findAllAthletesAtiveSignedByTeam(Team homeTeam);


}