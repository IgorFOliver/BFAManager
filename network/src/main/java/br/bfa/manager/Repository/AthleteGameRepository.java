package br.bfa.manager.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.AthleteGame;
 
@RepositoryRestResource
public interface AthleteGameRepository extends CrudRepository<AthleteGame, Long> {

	AthleteGame findById(Long id);
	
 
}