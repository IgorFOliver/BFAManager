package br.bfa.manager.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import br.bfa.manager.Enum.ImportsEvaluation;
import lombok.Getter;
import lombok.Setter;

@Entity
@EnableJpaAuditing
public class Evaluation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	private Long id;
	
	@Getter @Setter
	@OneToOne
	private Imports imports;

	@Column(length = 50000)
	@Getter @Setter
	private String evaluation;
	
	@Getter @Setter 
	private ImportsEvaluation evaluationStatus;
	
	@Getter @Setter
	@OneToOne
	private Account userResponsible;
}