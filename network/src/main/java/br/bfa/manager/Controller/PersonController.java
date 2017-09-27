package br.bfa.manager.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.bfa.manager.Repository.PersonRepository;
import br.bfa.manager.entity.Person;

@Controller
public class PersonController {

	@Autowired
	PersonRepository personRepository;

	@RequestMapping(value = "/persons/")
	public String getAllPersons(Model model) {
		List<Person> persons = personRepository.findAll();
		model.addAttribute("persons", persons);
		return "/persons/lperson";
	}

	@RequestMapping("/person/{id}")
	public String newPerson() {
		return "/persons/nperson";
	}

	@RequestMapping("/persons/{page}")
	String partialHandler(@PathVariable("page") final String page) {
		return "/persons/" + page;
	}

}
