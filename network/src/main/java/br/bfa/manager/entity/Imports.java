package br.bfa.manager.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import br.bfa.manager.Enum.ImportsEvaluation;
import lombok.Getter;
import lombok.Setter;

@Entity
@EnableJpaAuditing
public class Imports {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	private Long id;
	
	@Getter @Setter
	@OneToMany
	private List<Evaluation> evaluations;
	
	@Column(length = 50000)
	@Getter @Setter
	private String evaluation;
	
	@Getter @Setter
	@OneToOne
	private Account userResponsible;

	@Getter @Setter
	@OneToOne
	private Athlete athlete;
	
	@Getter @Setter
	@OneToOne
	private Team team;
	
	@Getter @Setter
	@OneToOne
	private Conference conference;
	
	@Getter @Setter 
	private ImportsEvaluation evaluationStatus;
}