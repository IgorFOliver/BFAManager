package br.bfa.manager.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.Team;
 
@RepositoryRestResource
public interface AthleteRepository extends CrudRepository<Athlete, Long> {
	
	List<Athlete> findByname(String name);
	
	List<Athlete> findAllByStatus(Boolean ativo);
	
	Athlete findById(Long id);

	Athlete findByEmail(String email);

	Athlete findByLegalId2(String legalId2);

	Athlete findByLegalId(String legalId);
	
	List<Athlete> findAllByStatusAndTeamOrderByName(Boolean status, Team team);

//	List<Athlete> findAllByGamesAndTeamOrderByName(Game game, Team team);

	Integer countByContractSignedTrue();

	Integer countByStatusTrue();
	 
	@Query("select count(a.id) from Athlete a where a.team.id IN (select t.id from Team t where t.conference.id = ?1)")
	Integer countByConference(Long id);

	@Query("SELECT a FROM Athlete a where a.status = 'TRUE' and a.contractSigned = 'TRUE'")
	List<Athlete> findAllAthletesAtiveSigned();

	@Query("SELECT a FROM Athlete a where a.status = 'TRUE' and a.contractSigned = 'TRUE' and a.team = ?1 Order by a.name")
	List<Athlete> findAllAthletesAtiveSignedByTeam(Team team);
	
}