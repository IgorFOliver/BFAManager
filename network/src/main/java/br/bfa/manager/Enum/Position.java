package br.bfa.manager.Enum;

import lombok.Getter;
import lombok.Setter;

public enum Position {
	CENTER(1, "C", "Center"), 
	GUARD(2, "OG", "Guard"), 
	TACKLE(3, "OT", "Tackle"), 
	QUARTERBACK(4, "QB", "Quarterback"), 
	FULLBACK(5, "FB", "Full back"),
	HALFBACK(6, "RB", "Running Back"),
	TIGHTEND(7, "TE", "Tightend"),
	WIDERECEIVER(8, "WR", "Wide Receiver"),
	DEFENSIVEEND(9, "DE", "Defensive End"),
	DEFENSIVETACKLE(10, "DT", "Defensive Tackle"),
	NOSETACKLE(11, "NT", "Nose Tackle"),
	LINEBACKER(12, "LB", "Linebacker"),
	MIKE(13, "MB", "Mike"),
	SAM(14, "SB", "Sam"),
	WILL(15, "WB", "Will"),
	STRONGSAFETY(16, "SS", "Strong Safety"),
	FREESAFETY(17, "FS", "Free Safety"),
	CORNERBACK(18, "CB", "Cornerback"),
	KICKER(19, "K", "Kicker"),
	PUNTER(20, "P", "Punter"),
	KICKOFFRETURN(21, "KOR", "Kickoff Returner"),
	PUNTRETURN(22, "PR", "Punt Returner"),
	RETURNER(23, "R", "Returner"),
	LONGSNAPPER(24, "LS", "Longsnapper"),
	HOLDER(25, "HD", "Holder"),
	NA(26, "N/A", "N/A");

	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	private String sigla;

	@Getter
	@Setter
	private String name;

	Position(int id, String sigla, String name) {
		this.id = id;
		this.sigla = sigla;
		this.name = name;
	}
	
	Position(String name) {
		this.name = name;
	}
	
	Position() {}

	public static Position obterTipoPorCodigo(String position) {
		Position na = Position.NA;
		for (Position pos : Position.values()) {
			if (pos.getSigla().equals(position)) {
				return pos;
			}
		}
		return na;

	}
	
}
