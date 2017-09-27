package br.bfa.manager.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.bfa.manager.entity.Imports;
 
@RepositoryRestResource
public interface ImportsRepository extends CrudRepository<Imports, Long> {
	
	Imports findById(Long id);
	
	List<Imports> findAll();

	Imports findByAthleteId(Long id);

	@Query("SELECT i FROM Imports i WHERE i.athlete.id IN (SELECT a.id from Athlete a where status = 'TRUE' and contractSigned = 'TRUE')")
	List<Imports> findAllAtive();
	
}