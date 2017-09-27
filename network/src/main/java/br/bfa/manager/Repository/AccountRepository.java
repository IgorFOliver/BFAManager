package br.bfa.manager.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.bfa.manager.entity.Account;
 
@RepositoryRestResource
public interface AccountRepository extends CrudRepository<Account, Long> {
	
	List<Account> findByName(String name);
	
	List<Account> findAllByStatus(Boolean ativo);

	List<Account> findAllByStatusOrderByName(Boolean ativo);
	
	Account findById(Long id);

	Account findByUsernameContainingIgnoreCase(String username);
	
}