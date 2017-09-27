package br.bfa.manager.Repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.bfa.manager.entity.Person;
 
@RepositoryRestResource
public interface PersonRepository extends CrudRepository<Person, Long> {
	
	List<Person> findByname(String name);
	List<Person> findAll();
 
}