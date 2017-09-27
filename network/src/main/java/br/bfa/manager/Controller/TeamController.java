package br.bfa.manager.Controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletContext;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.Enum.Conference;
import br.bfa.manager.Forms.TeamForm;
import br.bfa.manager.Service.AccountService;
import br.bfa.manager.Service.AthleteService;
import br.bfa.manager.Service.ConferenceService;
import br.bfa.manager.Service.TeamService;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Team;
import lombok.Getter;
import lombok.Setter;

@Controller
public class TeamController {

	@Autowired
	TeamService teamService;

	@Autowired
	AthleteService athleteService;

	@Autowired
	AccountService accountService;
	
	@Autowired
	ConferenceService conferenceService;

	@Getter
	@Setter
	public TeamForm teamForm;

	@Autowired
	ServletContext context;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/teams/")
	public String getAllteams(Model model) {
		model.addAttribute("teams", teamService.findAllByStatusOrderByName(Boolean.TRUE));
		return "/teams/lTeam";
	}

	@RequestMapping(value = "/teams/view")
	public String viewTeamAthlete(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		Team team = teamService.findByEmail(name);
		if (team != null) {
			model.addAttribute("team", team);
			model.addAttribute("conferences", conferenceService.findAllByLeague(team.getConference().getLeague()));
			return "/teams/nTeam";
		} else {
			team = teamService.findByAthletesEmailIn(name);
			model.addAttribute("team", team);
			return "/teams/vTeam";
		}
	}

	@RequestMapping("/teams/edit/{id}")
	public String editTeam(Model model, @PathVariable Long id) {
		Team team = teamService.findById(id);
		model.addAttribute("team", team);
		model.addAttribute("conferences", conferenceService.findAllByLeague(team.getConference().getLeague()));
		return "/teams/nTeam";
	}

	@RequestMapping("/teams/view/{id}")
	public String viewTeam(Model model, @PathVariable Long id) {
		model.addAttribute("team", teamService.findById(id));
		return "/teams/vTeam";
	}

	@RequestMapping("/teams/delete/{id}")
	public String deleteTeam(Model model, @PathVariable Long id) {
		model.addAttribute("teams", teamService.findAllByStatusOrderByName(Boolean.TRUE));
		teamService.delete(id);
		return "/teams/lTeam";
	}

	@RequestMapping(value = "/teams/nTeam", method = RequestMethod.GET, params = "action=novo")
	public String newEntity(Model model) {
//		model.addAttribute("conferences", conferenceService.findAllByLeague(team.getConference().getLeague()));
		model.addAttribute("team", new Team());
		return "/teams/nTeam";
	}

	@RequestMapping(value = "/teams/nTeam", method = RequestMethod.GET, params = "action=import")
	public String importTeams(Model model) throws InvalidFormatException, IOException {
		teamForm = new TeamForm();
		teamForm.setTeams(new ArrayList<Team>());
		model.addAttribute("teamForm", teamForm);
		return "/teams/iTeam";
	}

	@PostMapping(value = "/teams/save")
	public String saveEntity(@RequestParam("file") MultipartFile file, @Validated Team team,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "/teams/nTeam";
		}
		
		String path = context.getRealPath("logos");
		teamService.save(path, file, team);
		model.addAttribute("teams", teamService.findAllByStatusOrderByName(Boolean.TRUE));

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		for (GrantedAuthority role : auth.getAuthorities()) {
			if (role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_CONFERENCE")
					|| role.getAuthority().equals("ROLE_LEAGUE")) {
				return "/teams/lTeam";

			}	
		}
		
		model.addAttribute("team", team);
		return "/teams/nTeam";

	}

	@PostMapping(value = "/teams/import", params = "action=import")
	public String importXls(@RequestParam("file") MultipartFile file, TeamForm teamForm, BindingResult bindingResult,
			Model model) {
		try {
			teamForm = new TeamForm();
			if (file.isEmpty()) {
				bindingResult.addError(new ObjectError("global",
						messageSource.getMessage("message.file", null, LocaleContextHolder.getLocale())));
				teamForm.setTeams(new ArrayList<Team>());
				return "/teams/iTeam";
			}
			teamForm.setTeams(teamService.carregarXls(file));
			model.addAttribute("teamForm", teamForm);
			return "/teams/iTeam";
		} catch (Exception e) {
			bindingResult.addError(new ObjectError("global", e.getMessage()));
		}

		return "/teams/iTeam";

	}

	@RequestMapping(value = "/teams/import", params = "action=save", method = RequestMethod.POST)
	public String saveTeamsImport(@ModelAttribute TeamForm teamForm, BindingResult bindingResult, Model model) {
		try {
			if (teamForm.getTeams() == null || teamForm.getTeams().isEmpty()) {
				bindingResult.addError(new ObjectError("global",
						messageSource.getMessage("message.save.imports", null, LocaleContextHolder.getLocale())));
				teamForm.setTeams(new ArrayList<Team>());
				return "/teams/iTeam";
			}

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String name = auth.getName();
			Account creator = accountService.findByUsername(name);

			String retorno = teamService.saveAll(teamForm.getTeams(), creator);
			model.addAttribute("teams", teamService.findAllByStatusOrderByName(Boolean.TRUE));
			return retorno;

		} catch (Exception e) {

		}

		model.addAttribute("teams", teamService.findAllByStatusOrderByName(Boolean.TRUE));
		return "/teams/lTeam";

	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true, 10));
	}

}
