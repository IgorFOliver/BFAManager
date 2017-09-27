package br.bfa.manager.Controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
import org.springframework.validation.FieldError;
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

import br.bfa.manager.Enum.Position;
import br.bfa.manager.Forms.AthleteDTO;
import br.bfa.manager.Forms.AthleteForm;
import br.bfa.manager.Service.AccountService;
import br.bfa.manager.Service.AthleteService;
import br.bfa.manager.Service.ConfigurationService;
import br.bfa.manager.Service.EmailService;
import br.bfa.manager.Service.TeamService;
import br.bfa.manager.Storage.XLSUtil;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.Configuration;
import br.bfa.manager.entity.Team;
import lombok.Getter;
import lombok.Setter;

@Controller
public class AthleteController {

	@Autowired
	AthleteService athleteService;

	@Autowired
	TeamService teamService;

	@Autowired
	AccountService accountService;

	@Autowired
	EmailService emailService;
	
	@Autowired
	ConfigurationService configurationService;

	@Getter
	@Setter
	public AthleteForm athleteForm;

	@Autowired
	ServletContext context;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/athletes/")
	public String getAllathletes(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("athletes", athleteService.findAllByStatusAndTeamOrderByName(Boolean.TRUE,
				teamService.findByEmail(auth.getName())));
		return "/athletes/lAthlete";

	}
	
	@RequestMapping("/athletes/mailAdmin")
	public String athletesToMail(Model model) {
		List<Athlete> athletes= athleteService.findAllAthletesAtiveSigned();
		String path = context.getRealPath("sumulas");
		emailService.sendSimpleMailSumula(XLSUtil.generateXlsAthletes(athletes, path));
		return "/imports/lImports";
	}
	
	@RequestMapping(value = "/athletes/rAthletes")
	public String athletesReports(Model model) {
		model.addAttribute("athleteDTO", loadAthleteDTO());
		return "/athletes/rAthlete";

	}

	private AthleteDTO loadAthleteDTO() {
		AthleteDTO aDTR = new AthleteDTO();
		aDTR.setNumberAthletes(athleteService.countByStatusTrue());
		aDTR.setAthletesContract(athleteService.countByContractSignedTrue());
		aDTR.setCoConference(athleteService.countByConference(new Long(10055)));
		aDTR.setSConference(athleteService.countByConference(new Long(10057)));
		aDTR.setSeConference(athleteService.countByConference(new Long(10056)));
		aDTR.setNoConference(athleteService.countByConference(new Long(10058)));
		
		List<Team> teams = teamService.findAllByStatusOrderByName(Boolean.TRUE);
		Map <String, Integer> map = new HashMap<String, Integer>();
		for(Team team : teams){
			map.put(team.getName(), team.getAthletes().size());
		}
		aDTR.setAthletesTeam(map);
		
		return aDTR;
	}

	public String viewTeamAthlete(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		Team team = teamService.findByEmail(name);
		if (team != null) {
			model.addAttribute("team", team);
			return "/teams/nTeam";
		} else {
			team = teamService.findByAthletesEmailIn(name);
			model.addAttribute("team", team);
			return "/teams/vTeam";
		}
	}

	@RequestMapping(value = "/athletes/view")
	public String viewAthlete(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		Athlete athlete = athleteService.findByEmail(name);
		if (athlete != null) {
			model.addAttribute("athlete", athlete);
			model.addAttribute("listCountries", getAllCountries());
			return "/athletes/nAthlete";

		}
		return "/athletes/vAthlete";
	}

	public List<Locale> getAllCountries() {
		List<Locale> listLocales = new ArrayList<Locale>();

		for (Locale local : Locale.getAvailableLocales()) {
			listLocales.add(local);
		}
		return listLocales;

	}

	@RequestMapping("/athletes/edit/{id}")
	public String editAthlete(Model model, @PathVariable Long id) {
		model.addAttribute("athlete", athleteService.findById(id));
		model.addAttribute("listCountries", getAllCountries());
		return "/athletes/nAthlete";
	}

	@RequestMapping("/athletes/view/{id}")
	public String viewAthlete(Model model, @PathVariable Long id) {
		model.addAttribute("athlete", athleteService.findById(id));
		return "/athletes/vAthlete";
	}

	@RequestMapping("/athletes/delete/{id}")
	public String deleteAthlete(Model model, @PathVariable Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("athletes", athleteService.findAllByStatusAndTeamOrderByName(Boolean.TRUE,
				teamService.findByEmail(auth.getName())));
		athleteService.delete(id);
		return "/athletes/lAthlete";
	}

	@RequestMapping(value = "/athletes/nAthlete", method = RequestMethod.GET, params = "action=novo")
	public String newEntity(Model model) {
		Athlete athlete = new Athlete();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Team team = teamService.findByEmail(auth.getName());
		athlete.setTeam(team);
		athlete.setPositions(new ArrayList<Position>());
		model.addAttribute("athlete", athlete);
		model.addAttribute("listCountries", getAllCountries());
		return "/athletes/nAthlete";
	}
	
	@RequestMapping(value = "/athletes/reload", method = RequestMethod.GET)
	public String reloadForm(Model model) {
		return "/athletes/nAthlete";
	}


	@RequestMapping(value = "/athletes/nAthlete", method = RequestMethod.GET, params = "action=import")
	public String importAthletes(Model model) throws InvalidFormatException, IOException {
		athleteForm = new AthleteForm();
		athleteForm.setAthletes(new ArrayList<Athlete>());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("athleteForm", athleteForm);
		model.addAttribute("listTeams", teamService.findAllByAuthorities((Account) auth.getPrincipal()));
		return "/athletes/iAthlete";
	}

	@PostMapping(value = "/athletes/save")
	public String saveEntity(@RequestParam("file") MultipartFile file, @Validated Athlete athlete,
			BindingResult bindingResult, Model model) {
		try {

			Configuration config = configurationService.findFirstByOrderById();

			if (athlete.getId() == null && config != null && !config.getRegisterConfiguration()) {
				bindingResult.addError(new ObjectError("global",
						messageSource.getMessage("error.register.experied", null, LocaleContextHolder.getLocale())));
				athleteForm.setAthletes(new ArrayList<Athlete>());
				return "/athletes/iAthlete";
			}

			String path = context.getRealPath("profilePics");
			String retorno = "/athletes/nAthlete";

			athlete.setEmail(athlete.getEmail().trim());
			athlete.setEmail(athlete.getEmail().toLowerCase());
			validateAthlete(athlete, bindingResult);

			if (bindingResult.hasErrors()) {
				model.addAttribute("athlete", athlete);
				model.addAttribute("listCountries", getAllCountries());
				return "/athletes/nAthlete";
			}

			athleteService.save(path, file, athlete);
			model.addAttribute("athletes",
					athleteService.findAllByStatusAndTeamOrderByName(Boolean.TRUE, athlete.getTeam()));

			if (hasRole("ROLE_TEAM")) {
				retorno = "/athletes/lAthlete";
			}

			return retorno;
		} catch (AddressException e) {
			model.addAttribute("listCountries", getAllCountries());
			bindingResult.addError(new FieldError("athlete", "email", messageSource.getMessage("error.email.valid",
					new String[] { athlete.getName() }, LocaleContextHolder.getLocale())));
			return "/athletes/nAthlete";
		} catch (Exception e) {
			model.addAttribute("listCountries", getAllCountries());
			return "/athletes/nAthlete";
		}
	}

	private void validateAthlete(Athlete athlete, BindingResult bindingResult) throws AddressException {

		Athlete aEmail = athleteService.findByEmail(athlete.getEmail());

		if (aEmail != null && !aEmail.getId().equals(athlete.getId())) {
			bindingResult.addError(new FieldError("athlete", "email", messageSource.getMessage("error.email.unique",
					new String[] { athlete.getName() }, LocaleContextHolder.getLocale())));
		}
		InternetAddress add = new InternetAddress(athlete.getEmail());
		add.validate();

		if (athlete.getLegalId() != null && !athlete.getLegalId().isEmpty()) {
			Athlete aLegalId = athleteService.findByLegalId(athlete.getLegalId());

			if (aLegalId != null && !aLegalId.getId().equals(athlete.getId())) {
				bindingResult
						.addError(new FieldError("athlete", "legalId", messageSource.getMessage("error.legalId.unique",
								new String[] { athlete.getName() }, LocaleContextHolder.getLocale())));
			}
		}

		Athlete aLegalId2 = athleteService.findByLegalId2(athlete.getLegalId2());

		if (aLegalId2 != null && !aLegalId2.getId().equals(athlete.getId())) {
			bindingResult
					.addError(new FieldError("athlete", "legalId2", messageSource.getMessage("error.legalId2.unique",
							new String[] { athlete.getName() }, LocaleContextHolder.getLocale())));
		}

		Configuration config = configurationService.findFirstByOrderById();

		if (athlete.getId() != null && config != null && !config.getRegisterConfiguration()) {
			
			Athlete abase = athleteService.findById(athlete.getId());
			if (!abase.getLegalId().equals(athlete.getLegalId())
					|| !abase.getLegalId2().equals(athlete.getLegalId2())) {
				bindingResult.addError(new ObjectError("global",
						messageSource.getMessage("error.register.experied", null, LocaleContextHolder.getLocale())));
				athleteForm.setAthletes(new ArrayList<Athlete>());
			}

		}
		
		if (athlete.getPlaceBirth().getLanguage() == null || athlete.getPlaceBirth().getLanguage().equalsIgnoreCase("none")) {
				athlete.setPlaceBirth(null);
				bindingResult
						.addError(new FieldError("athlete", "placeBirth", messageSource.getMessage("error.placeBirth.required",
								new String[] { athlete.getName() }, LocaleContextHolder.getLocale())));
		}

	}

	@PostMapping(value = "/athletes/import", params = "action=import")
	public String importXls(@RequestParam("file") MultipartFile file, AthleteForm athleteForm,
			BindingResult bindingResult, Model model) {
		try {
			if (file.isEmpty()) {
				bindingResult.addError(new ObjectError("global",
						messageSource.getMessage("message.file", null, LocaleContextHolder.getLocale())));
				athleteForm.setAthletes(new ArrayList<Athlete>());
				return "/athletes/iAthlete";
			}
			athleteForm.setAthletes(athleteService.carregarXls(file, bindingResult));
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			model.addAttribute("listTeams", teamService.findAllByAuthorities((Account) auth.getPrincipal()));
			model.addAttribute("athleteForm", athleteForm);
			return "/athletes/iAthlete";
		} catch (Exception e) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			model.addAttribute("listTeams", teamService.findAllByAuthorities((Account) auth.getPrincipal()));
			bindingResult.addError(new ObjectError("global",
					messageSource.getMessage("athlete.importFile.error", null, LocaleContextHolder.getLocale())));
		}

		return "/athletes/iAthlete";

	}

	@PostMapping(value = "/athletes/signContract")
	public String signContract(Athlete athlete, BindingResult bindingResult, Model model) {
		try {
			Athlete nAthlete = athleteService.findById(athlete.getId());
			nAthlete.setContractSigned(Boolean.TRUE);
			nAthlete.setDtContractSigned(new Date());
			athleteService.save(nAthlete);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("athlete", athlete);
			return "/accounts/signContract";
		}
		return "/index";

	}

	@RequestMapping(value = "/athletes/import", params = "action=save", method = RequestMethod.POST)
	public String saveAthletesImport(@ModelAttribute AthleteForm athleteForm, BindingResult bindingResult,
			Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Configuration config = configurationService.findFirstByOrderById();

		if (config != null && !config.getRegisterConfiguration()) {
			bindingResult.addError(new ObjectError("global",
					messageSource.getMessage("error.register.experied", null, LocaleContextHolder.getLocale())));
			athleteForm.setAthletes(new ArrayList<Athlete>());
			return "/athletes/iAthlete";
		}

		try {
			if (athleteForm.getAthletes() == null || athleteForm.getAthletes().isEmpty()) {
				bindingResult.addError(new ObjectError("global",
						messageSource.getMessage("message.save.imports", null, LocaleContextHolder.getLocale())));
				athleteForm.setAthletes(new ArrayList<Athlete>());
				return "/athletes/iAthlete";
			}
			if (athleteForm.getTeam() == null || athleteForm.getTeam().getId() == 0) {
				bindingResult.addError(new ObjectError("global",
						messageSource.getMessage("message.athlete.team", null, LocaleContextHolder.getLocale())));
				model.addAttribute("listTeams", teamService.findAllByAuthorities((Account) auth.getPrincipal()));
				return "/athletes/iAthlete";
			}

			String name = auth.getName();
			Account creator = accountService.findByUsername(name);

			String retorno = athleteService.saveAll(athleteForm.getAthletes(), creator, athleteForm.getTeam());
			model.addAttribute("athletes", athleteService.findAllByStatusAndTeamOrderByName(Boolean.TRUE,
					teamService.findByEmail(auth.getName())));
			return retorno;

		} catch (Exception e) {
			bindingResult.addError(new ObjectError("global",
					messageSource.getMessage("athlete.importFile.error", null, LocaleContextHolder.getLocale())));
		}

		model.addAttribute("athletes", athleteService.findAllByStatusAndTeamOrderByName(Boolean.TRUE,
				teamService.findByEmail(auth.getName())));
		return "/athletes/iAthlete";

	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true, 10));
	}

	@SuppressWarnings("unchecked")
	private boolean hasRole(String role) {
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities();
		boolean hasRole = false;
		for (GrantedAuthority authority : authorities) {
			hasRole = authority.getAuthority().equals(role);
			if (hasRole) {
				break;
			}
		}
		return hasRole;
	}

}
