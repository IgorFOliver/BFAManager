package br.bfa.manager.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
 
@Entity
public class Person {
 
    public Person(String name) {
		this.name = name;
	}
    
    public Person(){}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Getter @Setter
	private long id;
 
    @Getter @Setter
    private String name;
 
}