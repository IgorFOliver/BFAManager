package br.bfa.manager.Controller;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.Service.ConferenceService;
import br.bfa.manager.Service.LeagueService;
import br.bfa.manager.entity.Conference;

@Controller
public class ConferenceController {

	@Autowired
	ConferenceService conferenceService;

	@Autowired
	LeagueService leagueService;
	
	@Autowired
	ServletContext context;
	
	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/conferences/")
	public String getAllconferences(Model model) {
		model.addAttribute("conferences", conferenceService.findAll());
		return "/conferences/lConference";

	}
	
	@RequestMapping("/conferences/edit/{id}")
	public String editConference(Model model, @PathVariable Long id) {
		model.addAttribute("conference", conferenceService.findById(id));
		model.addAttribute("listLeagues", leagueService.findAllByStatus(Boolean.TRUE));
		return "/conferences/nConference";
	}

	@RequestMapping("/conferences/view/{id}")
	public String viewConference(Model model, @PathVariable Long id) {
		model.addAttribute("conference", conferenceService.findById(id));
		model.addAttribute("listLeagues", leagueService.findAllByStatus(Boolean.TRUE));
		return "/conferences/vConference";
	}

	@RequestMapping("/conferences/delete/{id}")
	public String deleteConference(Model model, @PathVariable Long id) {
		model.addAttribute("conferences", conferenceService.findAll());
		conferenceService.delete(id);
		return "/conferences/lConference";
	}

	@RequestMapping(value = "/conferences/nConference", method = RequestMethod.GET)
	public String newEntity(Model model) {
		model.addAttribute("conference", new Conference());
		model.addAttribute("listLeagues", leagueService.findAllByStatus(Boolean.TRUE));
		return "/conferences/nConference";
	}

	@PostMapping(value = "/conferences/save")
	public String saveEntity(@RequestParam("file") MultipartFile file, @Validated Conference conference,
			BindingResult bindingResult, Model model) {
		try {
			if(conference.getLeague() == null || conference.getLeague().getId() == 0){
				model.addAttribute("listLeagues", leagueService.findAllByStatus(Boolean.TRUE));
				bindingResult.addError(new FieldError("conference", "league", messageSource.getMessage("error.league.valid",
						new String[] { conference.getName() }, LocaleContextHolder.getLocale())));
				return "/conferences/nConference";
			}
			conference.setLeague(leagueService.findById(conference.getLeague().getId()));
			
			String path = context.getRealPath("logos");
			
			conferenceService.save(file, path, conference);
			model.addAttribute("conferences", conferenceService.findAll());
			return "/conferences/lConference";
		} catch (Exception e) {
			model.addAttribute("listLeagues", leagueService.findAllByStatus(Boolean.TRUE));
			return "/conferences/nConference";
		}
	}


}
