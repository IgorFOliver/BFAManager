package br.bfa.manager.Controller;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import br.bfa.manager.Service.ConfigurationService;

@Controller
public class ConfigurationController {

	@Autowired
	ConfigurationService configurationService;

	@Autowired
	ServletContext context;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/configurations/")
	public String getAllconfigurations(Model model) {
		return "/configurations/lConfiguration";

	}

}
