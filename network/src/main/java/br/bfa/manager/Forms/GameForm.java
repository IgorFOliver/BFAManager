package br.bfa.manager.Forms;

import java.util.ArrayList;

import br.bfa.manager.entity.Game;
import br.bfa.manager.entity.Team;
import lombok.Getter;
import lombok.Setter;

public class GameForm {
	
	@Getter @Setter
	private Game game;
	
	@Getter @Setter
	private ArrayList<Game> games;
	
	@Getter @Setter
	private Team teamFilter;
	
	@Getter @Setter
	private Long teamId;

}
