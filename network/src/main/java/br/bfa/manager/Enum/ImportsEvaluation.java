package br.bfa.manager.Enum;

import lombok.Getter;
import lombok.Setter;

public enum ImportsEvaluation {
	SUBMITED(1, "Submetido"), 
	REJECTED(3, "Rejeitado"), 
	APPROVED(4, "Aprovado");

	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	private String name;

	ImportsEvaluation(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static ImportsEvaluation obterTipoPorCodigo(String name) {
		for (ImportsEvaluation conference : ImportsEvaluation.values()) {
			if (conference.getName().equals(name)) {
				return conference;
			}
		}
		return null;

	}

}
