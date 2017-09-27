package br.bfa.manager.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.Getter;
import lombok.Setter;

@Entity
@EnableJpaAuditing
public class League {

	public League(String name) {
		this.name = name;
	}

	public League() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	private Long id;

	@NotNull
	@Size(min = 1, max = 150)
	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String logo;
	
	@Getter
	@Setter
	private String facebook;
	
	@Getter
	@Setter
	private String instagram;
	
	@Getter
	@Setter
	private String twitter;
	
	@Getter
	@Setter
	private String site;
	
	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private Boolean status;
	
    @OneToMany(mappedBy="league")
    @Getter @Setter
    private List<Conference> conferences;

}