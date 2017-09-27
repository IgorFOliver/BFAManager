package br.bfa.manager.Enum;

import lombok.Getter;
import lombok.Setter;

public enum Conference {
	NORDESTE(1, "NO", "Nordeste"), NORTE(2, "N", "Norte"), CENTROOESTE(3, "CO", "Centro Oeste"), SUDESTE(4, "SE",
			"Sudeste"), SUL(5, "S", "Sul");

	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	private String sigla;

	@Getter
	@Setter
	private String name;
	
	Conference(int id, String sigla, String name) {
		this.id = id;
		this.sigla = sigla;
		this.name = name;
	}
	
	
	public static Conference obterTipoPorCodigo(String sigla){
		for(Conference conference : Conference.values()){
			if(conference.getSigla().equals(sigla)){
				return conference;
			}
		}
		return null;
		
	}

}
