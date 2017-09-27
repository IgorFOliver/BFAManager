package br.bfa.manager.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.bfa.manager.entity.League;
 
@RepositoryRestResource
public interface LeagueRepository extends CrudRepository<League, Long> {
	
	List<League> findByname(String name);
	
	List<League> findAllByStatus(Boolean ativo);
	
	League findById(Long id);
	
	List<League> findAll();
	
}