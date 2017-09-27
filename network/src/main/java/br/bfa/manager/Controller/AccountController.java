package br.bfa.manager.Controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.bfa.manager.Service.AccountService;
import br.bfa.manager.Service.AthleteService;
import br.bfa.manager.Service.TeamService;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.Role;
import br.bfa.manager.entity.Team;

@Controller
public class AccountController {

	@Autowired
	AccountService accountService;
	
	@Autowired
	AthleteService athleteService;
	
	@Autowired
	TeamService teamService;

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	ServletContext context;
	
	@Autowired
	private PasswordEncoder passwordEncoder;


	@RequestMapping(value = "/accounts/")
	public String getAllaccounts(Model model) {
		model.addAttribute("accounts", accountService.findAllByStatusOrderByName(Boolean.TRUE));
		return "/accounts/lAccount";
	}
	
	@RequestMapping(value = "/")
	public String index(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Athlete athlete = athleteService.findByEmail(auth.getName().toLowerCase().trim());
		if(athlete != null && (athlete.getContractSigned() == null || !athlete.getContractSigned())){
			model.addAttribute("athlete", athlete);
			return "/accounts/signContract";
		}
		
		return "/index";
	}
	
	

	@RequestMapping(value = "/accounts/register")
	public String register(Model model) {
		model.addAttribute("account", new Account());
		return "/accounts/register";
	}

	@RequestMapping(value = "/accounts/recoverPassword")
	public String recoverPassword(Model model) {
		model.addAttribute("account", new Account());
		return "/accounts/recoverPassword";
	}

	@PostMapping(value = "/accounts/recoverAccount")
	public String sentRecoverPassword(Model model, Account account, BindingResult bindingResult) {
		Account accountRegister = accountService.findByUsername(account.getUsername());
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_YEAR, -1);

			if (accountRegister != null) {
				if (accountRegister.getLastReset() != null && accountRegister.getLastReset().after(calendar.getTime())) {
					FieldError fieldError = new FieldError("account", "lastReset",
							messageSource.getMessage("error.account.lastReset", null, LocaleContextHolder.getLocale()));
					bindingResult.addError(fieldError);
					model.addAttribute("success", false);
				} else {
					accountService.recoverPassword(accountRegister);
					model.addAttribute("success", true);
					
				}

			} else {
				FieldError fieldError = new FieldError("account", "username",
						messageSource.getMessage("error.account.notFound", null, LocaleContextHolder.getLocale()));
				bindingResult.addError(fieldError);
				model.addAttribute("success", false);
			}

		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return "/accounts/recoverPassword";
	}
	
	@PostMapping(value = "/accounts/registerAccount")
	public String registerEntity(Account account, BindingResult bindingResult, Model model) {
		account.setUsername(account.getUsername().toLowerCase());
		
		if (!account.getPassword().equals(account.getPasswordConfirm())) {
			FieldError fieldError = new FieldError("account", "confirmPassword", "Password must be equal");
			bindingResult.addError(fieldError);
		}
		
		Account oldAccount = accountService.findByUsername(account.getUsername());
		
		if (oldAccount != null) {
			FieldError fieldError = new FieldError("account", "username",
					messageSource.getMessage("error.account.username.exists", null, LocaleContextHolder.getLocale()));
			bindingResult.addError(fieldError);
		}

		if (bindingResult.hasErrors()) {
			return "/accounts/register";
		}

		try {
			
			List<Role> authorities = new ArrayList<Role>();

			Athlete athlete = athleteService.findByEmail(account.getUsername().trim());
			if(athlete != null){
				authorities.add(new Role("ROLE_ATHLETE"));
				model.addAttribute("athlete", athlete);
			}
			
			
			Team team = teamService.findByEmail(account.getUsername().trim());
			if(team != null){
				authorities.add(new Role("ROLE_TEAM"));
			}
			
			
			UserDetails accountAuth = new Account(account.getUsername(), passwordEncoder.encode(account.getPassword()), authorities);
			Authentication authentication = new UsernamePasswordAuthenticationToken(accountAuth, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			accountService.save(account);
			
			if(athlete != null && athlete.getId() != null){
				return "/accounts/signContract";
			}else if(team == null){ 
				return "/accounts/noTeam";
			}
			return "/index";
			
			
		} catch (Exception e) {
			FieldError fieldError = new FieldError("account", "confirmPassword",
					messageSource.getMessage("error.account.password.notEqual", null, LocaleContextHolder.getLocale()));
			bindingResult.addError(fieldError);
			return "/accounts/register";
		}

	}

	@RequestMapping("/accounts/edit/{id}")
	public String editAccount(Model model, @PathVariable Long id) {
		model.addAttribute("account", accountService.findById(id));
		return "/accounts/eAccount";
	}

	@RequestMapping("/accounts/edit/")
	public String editAccountMenu(Model model) {
		return "/accounts/eAccount";
	}

	@RequestMapping("/accounts/view/{id}")
	public String viewAccount(Model model, @PathVariable Long id) {
		model.addAttribute("account", accountService.findById(id));
		return "/accounts/vAccount";
	}
	
	@RequestMapping("/accounts/vAccount")
	public String vAccount(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		model.addAttribute("account", accountService.findByUsername(name));
		return "/accounts/vAccount";
	}

	@RequestMapping("/accounts/delete/{id}")
	public String deleteAccount(Model model, @PathVariable Long id) {
		model.addAttribute("accounts", accountService.findAllByStatusOrderByName(Boolean.TRUE));
		accountService.delete(id);
		return "/accounts/lAccount";
	}

	@RequestMapping(value = "/accounts/nAccount", method = RequestMethod.GET)
	public String newEntity(Model model) {
		model.addAttribute("account", new Account());
		return "/accounts/nAccount";
	}

	@PostMapping(value = "/accounts/save")
	public String saveEntity(@Validated Account account, BindingResult bindingResult, Model model) {

		if (!account.getPassword().equals(account.getPasswordConfirm())) {
			FieldError fieldError = new FieldError("account", "confirmPassword",
					messageSource.getMessage("error.account.password.notEqual", null, LocaleContextHolder.getLocale()));
			bindingResult.addError(fieldError);
		}

		if (bindingResult.hasErrors()) {
			return "/accounts/nAccount";
		}

		try {
			accountService.save(account);
			model.addAttribute("accounts", accountService.findAllByStatusOrderByName(Boolean.TRUE));
			return "/accounts/lAccount";
		} catch (Exception e) {
			FieldError fieldError = new FieldError("account", "username", messageSource
					.getMessage("error.account.usernameRegistered", null, LocaleContextHolder.getLocale()));
			bindingResult.addError(fieldError);
		}

		return "/accounts/nAccount";
	}
	
	
	@PostMapping(value = "/accounts/changePassword")
	public String changePassword(Account account, BindingResult bindingResult, Model model) {
		Account cAccount = accountService.findById(account.getId());
		if (!account.getPassword().equals(account.getPasswordConfirm())) {
			FieldError fieldError = new FieldError("account", "confirmPassword",
					messageSource.getMessage("error.account.password.notEqual", null, LocaleContextHolder.getLocale()));
			bindingResult.addError(fieldError);
		}

		if (bindingResult.hasErrors()) {
			return "/accounts/vAccount";
		}

		try {
			cAccount.setPassword(account.getPassword());
			accountService.save(cAccount);
			model.addAttribute("account", cAccount);
			return "/accounts/vAccount";
		} catch (Exception e) {
			FieldError fieldError = new FieldError("account", "username", messageSource
					.getMessage("error.account.usernameRegistered", null, LocaleContextHolder.getLocale()));
			bindingResult.addError(fieldError);
		}

		return "/accounts/vAccount";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true, 10));
	}

}
