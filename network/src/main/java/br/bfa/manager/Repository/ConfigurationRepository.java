package br.bfa.manager.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.bfa.manager.entity.Configuration;
 
@RepositoryRestResource
public interface ConfigurationRepository extends CrudRepository<Configuration, Long> {

	Configuration findFirstByOrderById();
	
	
}