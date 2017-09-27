package br.bfa.manager.Enum;

import lombok.Getter;
import lombok.Setter;

public enum BloodType {
	AP(1, "A+", "A Positivo"), 
	AN(2, "A-", "A Negativo"), 
	BP(3, "B+", "B Positivo"), 
	BN(4, "B-", "B Negativo"), 
	ABP(5, "AB+", "AB Positivo"),
	ABN(6, "AB-", "AB Negativo"),
	OP(7, "O+", "O Positivo"),
	ON(8, "O-", "O Negativo"),
	NA(9, "N/A", "Não Informado");

	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	private String sigla;

	@Getter
	@Setter
	private String name;

	BloodType(int id, String sigla, String name) {
		this.id = id;
		this.sigla = sigla;
		this.name = name;
	}

	public static BloodType obterTipoPorCodigo(String blood) {
		for (BloodType bType : BloodType.values()) {
			if (bType.getSigla().equals(blood)) {
				return bType;
			}
		}
		return null;

	}

}
