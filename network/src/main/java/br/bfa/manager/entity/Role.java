package br.bfa.manager.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Role implements GrantedAuthority{

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Getter @Setter
	private Long id;
	
	@Getter @Setter
    private String name;
     
    public Role(String name) {
		this.name = name;
	}
    
    public Role() {}

	public String getAuthority() {
        return this.name;
    }
}