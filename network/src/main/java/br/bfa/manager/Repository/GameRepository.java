package br.bfa.manager.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.bfa.manager.entity.Game;
import br.bfa.manager.entity.Team;
 
@RepositoryRestResource
public interface GameRepository extends CrudRepository<Game, Long> {
	
	List<Game> findAll();
	
	Game findById(Long id);

	List<Game> findAllByOrderByDtGameAsc();

	@Query("SELECT g FROM Game g where g.homeTeam = ?1 or g.awayTeam = ?1 Order by g.dtGame")
	List<Game> findAllByTeamByOrderByDtGame(Team team);

}