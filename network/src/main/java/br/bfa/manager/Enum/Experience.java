package br.bfa.manager.Enum;

import lombok.Getter;
import lombok.Setter;

public enum Experience {
	NONE(1, 0, "Nenhuma"),
	AMERICA(2, 1, "Central America/South America/Africa"),
	HIGHSCHOOL(3, 4, "High-school/Europe Leagues"), 
	COLLEGE(4, 5, "NAIA/NCAA/College Leagues"), 
	NFL(5, 8, "NFL/CFL"); 

	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	private Integer points;

	@Getter
	@Setter
	private String name;

	Experience(int id, Integer points, String name) {
		this.id = id;
		this.points = points;
		this.name = name;
	}

	public static Experience obterTipoPorCodigo(String name) {
		for (Experience experience : Experience.values()) {
			if (experience.getName().equals(name)) {
				return experience;
			}
		}
		return null;

	}

}
