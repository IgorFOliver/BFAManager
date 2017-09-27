package br.bfa.manager.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.bfa.manager.entity.Official;
 
@RepositoryRestResource
public interface OfficialRepository extends CrudRepository<Official, Long> {
	
	List<Official> findByname(String name);
	
	List<Official> findAllByStatus(Boolean ativo);
	
	Official findById(Long id);

	Official findByEmail(String email);

	Official findByLegalId2(String legalId2);

	Official findByLegalId(String legalId);
	
	List<Official> findAllByStatusOrderByName(Boolean status);
	
}