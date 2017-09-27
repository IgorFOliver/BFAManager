package br.bfa.manager.Controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.bfa.manager.Enum.ImportsEvaluation;
import br.bfa.manager.Service.AccountService;
import br.bfa.manager.Service.AthleteService;
import br.bfa.manager.Service.EmailService;
import br.bfa.manager.Service.ImportsService;
import br.bfa.manager.Service.LeagueService;
import br.bfa.manager.Service.TeamService;
import br.bfa.manager.Storage.XLSUtil;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.Imports;
import br.bfa.manager.entity.League;

@Controller
public class ImportsController {

	@Autowired
	LeagueService leagueService;

	@Autowired
	AthleteService athleteService;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	TeamService teamService;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	ImportsService importsService;
	
	@Autowired
	ServletContext context;
	
	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/imports/")
	public String getAllImports(Model model) {
		model.addAttribute("listImports", importsService.findAllImportsAtive());
		return "/imports/lImports";

	}

	@RequestMapping("/imports/delete/{id}")
	public String deleteImport(Model model, @PathVariable Long id) {
		importsService.delete(id);
		model.addAttribute("listImports", importsService.findAllImportsAtive());
		return "/imports/lImports";
	}
	
	
	@RequestMapping("/imports/eImport/{id}")
	public String viewAthlete(Model model, @PathVariable Long id) {
		Imports imports = loadImport(id);
		model.addAttribute("imports", imports);
		
		return "/imports/eImport";
	}
	
	@RequestMapping("/imports/mailAdmin")
	public String importsToMail(Model model) {
		List<Imports> imports = importsService.findAllImportsAtive();
		String path = context.getRealPath("sumulas");
		emailService.sendSimpleMailSumula(XLSUtil.generateXLSImports(imports, path));
		model.addAttribute("listImports", imports);
		return "/imports/lImports";
	}
	
	

	private Imports loadImport(Long id) {
		Imports imports = importsService.findById(id);
		if(imports == null){
			imports = new Imports();
			Athlete athlete = athleteService.findById(id);
			imports.setAthlete(athlete);
			imports.setEvaluationStatus(ImportsEvaluation.SUBMITED);
			imports.setTeam(athlete.getTeam());
		}
		
		String[] locales = Locale.getISOCountries();
		for (String countryCode : locales) {
			if(countryCode.toString().equalsIgnoreCase(imports.getAthlete().getPlaceBirth().getLanguage())){
				imports.getAthlete().setPlaceBirth(new Locale(imports.getAthlete().getPlaceBirth().getLanguage(), countryCode));
			}
			
		}
		
		return imports;
	}
	
	@RequestMapping(value = "/imports/checkTeam", method = RequestMethod.GET)
	public String newEntity(Model model) {
		model.addAttribute("league", new League());
		return "/leagues/nLeague";
	}

	@PostMapping(value = "/imports/save")
	public String saveEntity(@Validated Imports imports,
			BindingResult bindingResult, Model model) {
		try {
			if(imports.getEvaluation() == null || imports.getEvaluation().isEmpty()){
				bindingResult.addError(new FieldError("imports", "evaluation", messageSource.getMessage("error.evaluation.required",
						new String[] { imports.getAthlete().getName() }, LocaleContextHolder.getLocale())));
				model.addAttribute("imports", loadImport(imports.getId()));
				return "/imports/eImport";
			}
			if(imports.getAthlete().getExperience() == null){
				bindingResult.addError(new FieldError("imports", "experience", messageSource.getMessage("error.experience.required",
						new String[] { imports.getAthlete().getName() }, LocaleContextHolder.getLocale())));
				model.addAttribute("imports", imports);
				return "/imports/eImport";
			}
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String name = auth.getName();
			Account account = accountService.findByUsername(name);
			Athlete athlete = athleteService.findById(imports.getAthlete().getId());
			athlete.setExperience(imports.getAthlete().getExperience());
			imports.setAthlete(athlete);
			imports.setEvaluationStatus(ImportsEvaluation.APPROVED);
			imports.setUserResponsible(account);
			importsService.save(imports);
			return getAllImports(model);
		} catch (Exception e) {
			model.addAttribute("imports", imports);
			return "/imports/eImport";
		}
	}


}
