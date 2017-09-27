package br.bfa.manager.Forms;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class AthleteDTO {
	
	@Getter @Setter
	private Integer numberAthletes;
	
	@Getter @Setter
	private Integer athletesContract;
	
	@Getter @Setter
	private Integer coConference;
	
	@Getter @Setter
	private Integer sConference;
	
	@Getter @Setter
	private Integer seConference;
	
	@Getter @Setter
	private Integer noConference;
	
	@Getter @Setter
	Map<String, Integer> athletesTeam;

}
