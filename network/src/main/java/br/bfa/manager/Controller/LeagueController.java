package br.bfa.manager.Controller;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.Service.LeagueService;
import br.bfa.manager.entity.League;

@Controller
public class LeagueController {

	@Autowired
	LeagueService leagueService;

	@Autowired
	ServletContext context;

	@RequestMapping(value = "/leagues/")
	public String getAllleagues(Model model) {
		model.addAttribute("leagues", leagueService.findAll());
		return "/leagues/lLeague";

	}
	
	@RequestMapping("/leagues/edit/{id}")
	public String editLeague(Model model, @PathVariable Long id) {
		model.addAttribute("league", leagueService.findById(id));
		return "/leagues/nLeague";
	}

	@RequestMapping("/leagues/view/{id}")
	public String viewLeague(Model model, @PathVariable Long id) {
		model.addAttribute("league", leagueService.findById(id));
		return "/leagues/vLeague";
	}

	@RequestMapping("/leagues/delete/{id}")
	public String deleteLeague(Model model, @PathVariable Long id) {
		model.addAttribute("leagues", leagueService.findAll());
		leagueService.delete(id);
		return "/leagues/lLeague";
	}

	@RequestMapping(value = "/leagues/nLeague", method = RequestMethod.GET)
	public String newEntity(Model model) {
		model.addAttribute("league", new League());
		return "/leagues/nLeague";
	}

	@PostMapping(value = "/leagues/save")
	public String saveEntity(@RequestParam("file") MultipartFile file, @Validated League league,
			BindingResult bindingResult, Model model) {
		try {
			String path = context.getRealPath("logos");
			leagueService.save(file, path, league);
			model.addAttribute("leagues", leagueService.findAll());
			return "/leagues/lLeague";
		} catch (Exception e) {
			return "/leagues/nLeague";
		}
	}


}
