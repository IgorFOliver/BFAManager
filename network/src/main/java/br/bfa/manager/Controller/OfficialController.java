package br.bfa.manager.Controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

import br.bfa.manager.Enum.PositionOfficial;
import br.bfa.manager.Forms.OfficialForm;
import br.bfa.manager.Service.AccountService;
import br.bfa.manager.Service.OfficialService;
import br.bfa.manager.Service.TeamService;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Official;
import lombok.Getter;
import lombok.Setter;

@Controller
public class OfficialController {

	@Autowired
	OfficialService officialService;

	@Autowired
	TeamService teamService;

	@Autowired
	AccountService accountService;

	@Getter
	@Setter
	public OfficialForm officialForm;

	@Autowired
	ServletContext context;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/officials/")
	public String getAllofficials(Model model) {
		model.addAttribute("officials", officialService.findAllByStatusOrderByName(Boolean.TRUE));
		return "/officials/lOfficial";

	}

	@RequestMapping(value = "/officials/view")
	public String viewOfficial(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		Official official = officialService.findByEmail(name);
		if (official != null) {
			model.addAttribute("official", official);
			model.addAttribute("listCountries", getAllCountries());
			return "/officials/nOfficial";

		}
		return "/officials/vOfficial";
	}

	public List<Locale> getAllCountries() {
		List<Locale> listLocales = new ArrayList<Locale>();
		String[] locales = Locale.getISOCountries();

		for (String countryCode : locales) {
			Locale obj = new Locale("", countryCode);
			listLocales.add(obj);
		}
		return listLocales;

	}

	@RequestMapping("/officials/edit/{id}")
	public String editOfficial(Model model, @PathVariable Long id) {
		model.addAttribute("official", officialService.findById(id));
		model.addAttribute("listCountries", getAllCountries());
		return "/officials/nOfficial";
	}

	@RequestMapping("/officials/view/{id}")
	public String viewOfficial(Model model, @PathVariable Long id) {
		model.addAttribute("official", officialService.findById(id));
		return "/officials/vOfficial";
	}

	@RequestMapping("/officials/delete/{id}")
	public String deleteOfficial(Model model, @PathVariable Long id) {
		model.addAttribute("officials", officialService.findAllByStatusOrderByName(Boolean.TRUE));
		officialService.delete(id);
		return "/officials/lOfficial";
	}

	@RequestMapping(value = "/officials/nOfficial", method = RequestMethod.GET, params = "action=novo")
	public String newEntity(Model model) {
		Official official = new Official();
		official.setPositions(new ArrayList<PositionOfficial>());
		model.addAttribute("official", official);
		model.addAttribute("listCountries", getAllCountries());
		return "/officials/nOfficial";
	}

	@RequestMapping(value = "/officials/nOfficial", method = RequestMethod.GET, params = "action=import")
	public String importOfficials(Model model) throws InvalidFormatException, IOException {
		officialForm = new OfficialForm();
		officialForm.setOfficials(new ArrayList<Official>());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("officialForm", officialForm);
		model.addAttribute("listTeams", teamService.findAllByAuthorities((Account) auth.getPrincipal()));
		return "/officials/iOfficial";
	}

	@PostMapping(value = "/officials/save")
	public String saveEntity(@RequestParam("file") MultipartFile file, @Validated Official official,
			BindingResult bindingResult, Model model) {
		try {
			String path = context.getRealPath("profilePics");
			String retorno = "/officials/nOfficial";

			validateOfficial(official, bindingResult);

			if (bindingResult.hasErrors()) {
				model.addAttribute("official", official);
				model.addAttribute("listCountries", getAllCountries());
				return "/officials/nOfficial";
			}

			officialService.save(path, file, official);
			model.addAttribute("officials", officialService.findAllByStatusOrderByName(Boolean.TRUE));

			if (hasRole("ROLE_OFFICIAL")) {
				retorno = "/officials/lOfficial";
			}

			return retorno;
		} catch (AddressException e) {
			model.addAttribute("listCountries", getAllCountries());
			bindingResult.addError(new FieldError("official", "email", messageSource.getMessage("error.email.valid",
					new String[] { official.getName() }, LocaleContextHolder.getLocale())));
			return "/officials/nOfficial";
		} catch (Exception e) {
			model.addAttribute("listCountries", getAllCountries());
			return "/officials/nOfficial";
		}
	}

	private void validateOfficial(Official official, BindingResult bindingResult) throws AddressException {

		Official aEmail = officialService.findByEmail(official.getEmail());

		if (aEmail != null && !aEmail.getId().equals(official.getId())) {
			bindingResult.addError(new FieldError("official", "email", messageSource.getMessage("error.email.unique",
					new String[] { official.getName() }, LocaleContextHolder.getLocale())));
		}
		InternetAddress add = new InternetAddress(official.getEmail());
		add.validate();

		Official aLegalId = officialService.findByLegalId(official.getLegalId());

		if (aLegalId != null && !aLegalId.getId().equals(official.getId())) {
			bindingResult
					.addError(new FieldError("official", "legalId", messageSource.getMessage("error.legalId.unique",
							new String[] { official.getName() }, LocaleContextHolder.getLocale())));
		}

		Official aLegalId2 = officialService.findByLegalId2(official.getLegalId2());

		if (aLegalId2 != null && !aLegalId2.getId().equals(official.getId())) {
			bindingResult
					.addError(new FieldError("official", "legalId2", messageSource.getMessage("error.legalId2.unique",
							new String[] { official.getName() }, LocaleContextHolder.getLocale())));
		}

	}

	@PostMapping(value = "/officials/import", params = "action=import")
	public String importXls(@RequestParam("file") MultipartFile file, OfficialForm officialForm,
			BindingResult bindingResult, Model model) {
		try {
			if (file.isEmpty()) {
				bindingResult.addError(new ObjectError("global",
						messageSource.getMessage("message.file", null, LocaleContextHolder.getLocale())));
				officialForm.setOfficials(new ArrayList<Official>());
				return "/officials/iOfficial";
			}
			officialForm.setOfficials(officialService.carregarXls(file, bindingResult));
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			model.addAttribute("listTeams", teamService.findAllByAuthorities((Account) auth.getPrincipal()));
			model.addAttribute("officialForm", officialForm);
			return "/officials/iOfficial";
		} catch (Exception e) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			model.addAttribute("listTeams", teamService.findAllByAuthorities((Account) auth.getPrincipal()));
			bindingResult.addError(new ObjectError("global",
					messageSource.getMessage("official.importFile.error", null, LocaleContextHolder.getLocale())));
		}

		return "/officials/iOfficial";

	}

	@PostMapping(value = "/officials/signContract")
	public String signContract(Official official, BindingResult bindingResult, Model model) {
		try {
			Official nOfficial = officialService.findById(official.getId());
			officialService.save(nOfficial);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("official", official);
			return "/accounts/signContract";
		}
		return "/index";

	}

	@RequestMapping(value = "/officials/import", params = "action=save", method = RequestMethod.POST)
	public String saveOfficialsImport(@ModelAttribute OfficialForm officialForm, BindingResult bindingResult,
			Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		try {
			if (officialForm.getOfficials() == null || officialForm.getOfficials().isEmpty()) {
				bindingResult.addError(new ObjectError("global",
						messageSource.getMessage("message.save.imports", null, LocaleContextHolder.getLocale())));
				officialForm.setOfficials(new ArrayList<Official>());
				return "/officials/iOfficial";
			}

			String name = auth.getName();
			Account creator = accountService.findByUsername(name);

			String retorno = officialService.saveAll(officialForm.getOfficials(), creator);
			model.addAttribute("officials", officialService.findAllByStatusOrderByName(Boolean.TRUE));
			return retorno;

		} catch (Exception e) {
			bindingResult.addError(new ObjectError("global",
					messageSource.getMessage("official.importFile.error", null, LocaleContextHolder.getLocale())));
		}

		model.addAttribute("officials", officialService.findAllByStatusOrderByName(Boolean.TRUE));
		return "/officials/iOfficial";

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
