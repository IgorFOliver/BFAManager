package br.bfa.manager.Enum;

import lombok.Getter;
import lombok.Setter;

public enum PositionOfficial {
	REFEREE(1, "R", "Referee"), 
	UMPIRE(2, "U", "Umpire"), 
	HEADLINESMAN(3, "HL", "Head Linesman"), 
	LINEJUDGE(4, "LJ", "Line Judge"), 
	FIELDJUDGE(5, "FJ", "Field Judge"),
	SIDEJUDGE(6, "SJ", "Side Judge"),
	BACKJUDGE(7, "BJ", "Back Judge"),
	NA(8, "NA", "Not Avaliable");

	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	private String sigla;

	@Getter
	@Setter
	private String name;

	PositionOfficial(int id, String sigla, String name) {
		this.id = id;
		this.sigla = sigla;
		this.name = name;
	}
	
	PositionOfficial(String name) {
		this.name = name;
	}
	
	PositionOfficial() {}

	public static PositionOfficial obterTipoPorCodigo(String position) {
		PositionOfficial na = PositionOfficial.NA;
		for (PositionOfficial pos : PositionOfficial.values()) {
			if (pos.getSigla().equals(position)) {
				return pos;
			}
		}
		return na;

	}
	
}
