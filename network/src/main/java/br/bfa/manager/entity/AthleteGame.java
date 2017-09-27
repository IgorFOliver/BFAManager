package br.bfa.manager.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.Getter;
import lombok.Setter;
 
@Entity
@EnableJpaAuditing
public class AthleteGame {
 
    public AthleteGame(){}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Getter @Setter
	private Long id;
	
    @Getter @Setter
    @ManyToOne
    private Game homeGame;
    
    @Getter @Setter
    @ManyToOne
    private Game awayGame;
    
    @Getter @Setter
    @OneToOne
    private Athlete athlete;
    
    @Getter @Setter
    @OneToOne
    private Team team;
    
    @Getter @Setter
    private Boolean played;
    
    @Getter @Setter
    private Boolean ejected;

 
}