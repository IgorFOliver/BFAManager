package br.bfa.manager.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.Getter;
import lombok.Setter;

@Entity
@EnableJpaAuditing
public class Game {

	public Game() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	private Long id;

	@Getter
	@Setter
	private Date dtGame;

	@Getter
	@Setter
	@OneToOne
	private Team homeTeam;

	@Getter
	@Setter
	@OneToOne
	private Team awayTeam;

	@Getter
	@Setter
	private String homeColor;
	
	@Getter
	@Setter
	private String gameDocs;

	@Getter
	@Setter
	private String awayColor;

	@Getter
	@Setter
	private String place;
	
	@Getter
	@Setter
	private Integer home1st;
	
	@Getter
	@Setter
	private Integer home2nd;
	
	@Getter
	@Setter
	private Integer home3rd;

	@Getter
	@Setter
	private Integer home4th;
	
	@Getter
	@Setter
	private Integer homeOT;

	@Getter
	@Setter
	private Integer away1st;
	
	@Getter
	@Setter
	private Integer away2nd;
	
	@Getter
	@Setter
	private Integer away3rd;

	@Getter
	@Setter
	private Integer away4th;
	
	@Getter
	@Setter
	private Integer awayOT;

	@Getter
	@Setter
	@OneToOne
	private Conference conference;

	@Getter @Setter
	@OneToMany(mappedBy = "homeGame")
	private List<AthleteGame> homeAthletesGame;
	
	@Getter @Setter
	@OneToMany(mappedBy = "awayGame")
	private List<AthleteGame> awayAthletesGame;

	@ElementCollection
	@JoinTable(name = "official_game", joinColumns = @JoinColumn(name = "official_Id"))
	@Getter
	@Setter
	private List<Official> officials;

	@CreatedBy
	@Getter
	@Setter
	@OneToOne
	private Account userCreator;

	@CreatedDate
	@Getter
	@Setter
	private Date dateCreate;

}