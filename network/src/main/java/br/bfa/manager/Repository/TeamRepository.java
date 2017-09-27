package br.bfa.manager.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.bfa.manager.Enum.Conference;
import br.bfa.manager.entity.Team;
 
@RepositoryRestResource
public interface TeamRepository extends CrudRepository<Team, Long> {
	
	List<Team> findByname(String name);
	
	List<Team> findAllByStatus(Boolean ativo);

	List<Team> findAllByStatusOrderByName(Boolean ativo);
	
	Team findById(Long id);

	Team findByEmail(String username);

	List<Team> findAllByConference(Conference conference);
	
	@Query( "select t FROM Team t, IN (t.athletes) a WHERE a.email = :email" )
	Team findByAthletesEmailIn(@Param("email") String email);

	@Query( "select t FROM Team t, IN (t.athletes) a WHERE a.placeBirth IS NOT NULL and a.placeBirth != 'br' and a.placeBirth != 'Brasil' and a.placeBirth != 'Brazil'" )
	List<Team> findAllTeamsWithImportsAtive();
	
}