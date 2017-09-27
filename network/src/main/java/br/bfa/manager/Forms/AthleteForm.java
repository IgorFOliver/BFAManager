package br.bfa.manager.Forms;

import java.util.ArrayList;

import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.Team;
import lombok.Getter;
import lombok.Setter;

public class AthleteForm {
	
	@Getter @Setter
	private ArrayList<Athlete> athletes;

	@Getter @Setter
	private Team team;
}
