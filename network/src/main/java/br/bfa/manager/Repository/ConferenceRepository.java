package br.bfa.manager.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.bfa.manager.entity.Conference;
import br.bfa.manager.entity.League;
 
@RepositoryRestResource
public interface ConferenceRepository extends CrudRepository<Conference, Long> {
	
	List<Conference> findByname(String name);
	
	List<Conference> findAllByStatus(Boolean ativo);
	
	Conference findById(Long id);
	
	List<Conference> findAll();

	List<Conference> findAllByLeague(League league);
	
}