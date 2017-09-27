package br.bfa.manager.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.Forms.GameForm;
import br.bfa.manager.Service.AccountService;
import br.bfa.manager.Service.AthleteService;
import br.bfa.manager.Service.EmailService;
import br.bfa.manager.Service.GameService;
import br.bfa.manager.Service.ImportsService;
import br.bfa.manager.Service.TeamService;
import br.bfa.manager.Storage.XLSUtil;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.AthleteGame;
import br.bfa.manager.entity.Game;
import br.bfa.manager.entity.Team;
import lombok.Getter;
import lombok.Setter;

@Controller
public class GameController {

	@Autowired
	GameService gameService;

	@Autowired
	TeamService teamService;

	@Autowired
	AthleteService athleteService;

	@Autowired
	AccountService accountService;

	@Autowired
	ImportsService importsService;

	@Autowired
	EmailService emailService;

	@Getter
	@Setter
	public GameForm gameForm;

	@Autowired
	ServletContext context;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/games/")
	public String getAllgames(Model model) {
		model.addAttribute("games", gameService.findAllByOrderByDtGame());
		model.addAttribute("gameForm", new GameForm());
		model.addAttribute("listTeams", teamService.findAllByStatus(Boolean.TRUE));
		model.addAttribute("listLeagues", new GameForm());

		return "/games/lGame";

	}

	@RequestMapping(value = "/games/viewTeam")
	public String getAllgamesByTeam(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account conta = (Account) auth.getPrincipal();
		Team team = teamService.findByEmail(conta.getUsername());
		model.addAttribute("games", gameService.findAllByTeamByOrderByDtGame(team));
		model.addAttribute("gameForm", new GameForm());
		model.addAttribute("listTeams", teamService.findAllByStatus(Boolean.TRUE));
		model.addAttribute("listLeagues", new GameForm());

		return "/games/lGame";

	}

	public List<File> gerarSumulas(Game game) {
		String path = context.getRealPath("");
		List<File> files = new ArrayList<>();
		files.add(new File(context.getRealPath(File.separator + "files" + File.separator + "CheckList.pdf")));
		files.add(new File(context.getRealPath(File.separator + "files" + File.separator + "Sumula.pdf")));
		List<Athlete> homeAthletes = athleteService.findAllAthletesAtiveSignedByTeam(game.getHomeTeam());
		List<Athlete> awayAthletes = athleteService.findAllAthletesAtiveSignedByTeam(game.getAwayTeam());

		files.add(XLSUtil.generateSumula(game.getHomeTeam(), path, homeAthletes));
		files.add(XLSUtil.generateSumula(game.getAwayTeam(), path, awayAthletes));
		return files;

	}

	@RequestMapping(value = "/games/sGame")
	public String showGamesTimeline(Model model) {
		model.addAttribute("games", gameService.findAllByOrderByDtGame());
		return "/games/sGame";

	}

	@RequestMapping("/games/edit/{id}")
	public String editGame(Model model, @PathVariable Long id) {
		Game game = gameService.findById(id);
		model.addAttribute("listTeams", teamService.findAllByStatus(Boolean.TRUE));
		model.addAttribute("gameForm", loadGameForm(game));
		return "/games/nGame";
	}

	@RequestMapping("/games/sheet/{id}")
	public String gameSheet(Model model, @PathVariable Long id) {
		Game game = gameService.findById(id);
		String dtGame = new SimpleDateFormat("dd/MM/yyyy").format(game.getDtGame());
		String str = "[BFA] Sumula Jogo - " + game.getHomeTeam().getName() + " vs " + game.getAwayTeam().getName()
				+ " Data: " + dtGame;
		emailService.sendMultipleFiles(game.getHomeTeam().getEmail(), str, gerarSumulas(game));
		model.addAttribute("games", gameService.findAllByOrderByDtGame());
		model.addAttribute("gameForm", new GameForm());
		model.addAttribute("listTeams", teamService.findAllByStatus(Boolean.TRUE));
		model.addAttribute("listLeagues", new GameForm());
		return "/games/lGame";
	}

	@RequestMapping(value = "/games/download/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public void downloadFile(@PathVariable Long id, HttpServletResponse response) {
		InputStream targetStream;
		String path = context.getRealPath("");
		Game game = gameService.findById(id);
		File initialFile = XLSUtil.zip(gerarSumulas(game), path + "Jogo.zip");
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=" + "Jogo.zip");
		try {
			targetStream = new FileInputStream(initialFile);
			org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
			response.flushBuffer();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/games/downloadFinalSheet/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public void downloadFinalSheet(@PathVariable Long id, HttpServletResponse response) {
		InputStream targetStream;
		String path = context.getRealPath("");
		Game game = gameService.findById(id);
		File initialFile = new File(path + File.separator + game.getGameDocs());
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=" + "Jogo.pdf");
		try {
			targetStream = new FileInputStream(initialFile);
			org.apache.commons.io.IOUtils.copy(targetStream, response.getOutputStream());
			response.flushBuffer();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/games/sendMailSheet/{id}")
	public String gameSendEmailSheet(Model model, @PathVariable Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account conta = (Account) auth.getPrincipal();
		Game game = gameService.findById(id);
		String dtGame = new SimpleDateFormat("dd/MM/yyyy").format(game.getDtGame());
		String str = "[BFA] Sumula Jogo - " + game.getHomeTeam().getName() + " vs " + game.getAwayTeam().getName()
				+ " Data: " + dtGame;
		emailService.sendMultipleFiles(conta.getUsername(), str, gerarSumulas(game));
		model.addAttribute("games", gameService.findAllByOrderByDtGame());
		model.addAttribute("gameForm", new GameForm());
		model.addAttribute("listTeams", teamService.findAllByStatus(Boolean.TRUE));
		model.addAttribute("listLeagues", new GameForm());
		return "/games/lGame";
	}

	private GameForm loadGameForm(Game game) {
		GameForm gameForm = new GameForm();
		gameForm.setGame(game);

		AthleteGame aGame;

		if (game.getHomeAthletesGame() == null || game.getHomeAthletesGame().isEmpty()) {
			List<Athlete> homeAthletes = athleteService.findAllByStatusAndTeamOrderByName(Boolean.TRUE, game.getHomeTeam());
			for (Athlete hAthlete : homeAthletes) {
				aGame = new AthleteGame();
				aGame.setAthlete(hAthlete);
				aGame.setHomeGame(game);
				aGame.setPlayed(Boolean.FALSE);
				game.getHomeAthletesGame().add(aGame);
			}

		}
		
		if (game.getAwayAthletesGame() == null || game.getAwayAthletesGame().isEmpty()) {
			List<Athlete> awayAthletes = athleteService.findAllByStatusAndTeamOrderByName(Boolean.TRUE, game.getAwayTeam());
			for (Athlete hAthlete : awayAthletes) {
				aGame = new AthleteGame();
				aGame.setAthlete(hAthlete);
				aGame.setAwayGame(game);
				aGame.setPlayed(Boolean.FALSE);
				game.getAwayAthletesGame().add(aGame);
			}

		}

		return gameForm;
	}

	@RequestMapping("/games/view/{id}")
	public String viewGame(Model model, @PathVariable Long id) {
		model.addAttribute("game", gameService.findById(id));
		return "/games/vGame";
	}

	@RequestMapping("/games/delete/{id}")
	public String deleteGame(Model model, @PathVariable Long id) {
		model.addAttribute("games", gameService.findAllByOrderByDtGame());
		gameService.delete(id);
		return "/games/lGame";
	}

	@RequestMapping(value = "/games/nGame", method = RequestMethod.GET, params = "action=novo")
	public String newEntity(Model model) {
		GameForm gameForm = new GameForm();
		model.addAttribute("gameForm", gameForm);
		model.addAttribute("teams", teamService.findAllByStatus(Boolean.TRUE));
		return "/games/nGame";
	}

	@RequestMapping(value = "/games/nGame", method = RequestMethod.GET, params = "action=import")
	public String importGames(Model model) throws InvalidFormatException, IOException {
		gameForm = new GameForm();
		gameForm.setGames(new ArrayList<Game>());
		model.addAttribute("gameForm", gameForm);
		model.addAttribute("listTeams", teamService.findAllByStatus(Boolean.TRUE));
		return "/games/iGame";
	}

	@PostMapping(value = "/games/save")
	public String saveEntity(@RequestParam("file") MultipartFile file, @Validated GameForm gameForm,
			BindingResult bindingResult, Model model) {
		try {
			String retorno = "/games/nGame";
			String path = context.getRealPath("gamesDocs");

			gameService.save(path, file, gameForm.getGame());
			model.addAttribute("games", gameService.findAllByOrderByDtGame());

			if (hasRole("ROLE_ADMIN")) {
				retorno = "/games/lGame";
			}

			return retorno;
		} catch (Exception e) {
			model.addAttribute("listTeams", teamService.findAllByStatus(Boolean.TRUE));
			return "/games/nGame";
		}
	}

	@PostMapping(value = "/games/import", params = "action=import")
	public String importXls(@RequestParam("file") MultipartFile file, GameForm gameForm, BindingResult bindingResult,
			Model model) {
		try {
			if (file.isEmpty()) {
				bindingResult.addError(new ObjectError("global",
						messageSource.getMessage("message.file", null, LocaleContextHolder.getLocale())));
				gameForm.setGames(new ArrayList<Game>());
				return "/games/iGame";
			}
			gameForm.setGames(gameService.carregarXls(file, bindingResult));
			model.addAttribute("listTeams", teamService.findAllByStatus(Boolean.TRUE));
			model.addAttribute("gameForm", gameForm);
			return "/games/iGame";
		} catch (Exception e) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			model.addAttribute("listTeams", teamService.findAllByAuthorities((Account) auth.getPrincipal()));
			bindingResult.addError(new ObjectError("global",
					messageSource.getMessage("game.importFile.error", null, LocaleContextHolder.getLocale())));
		}

		return "/games/iGame";

	}

	@RequestMapping(value = "/games/import", params = "action=save", method = RequestMethod.POST)
	public String saveGamesImport(@ModelAttribute GameForm gameForm, BindingResult bindingResult, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		try {
			if (gameForm.getGames() == null || gameForm.getGames().isEmpty()) {
				bindingResult.addError(new ObjectError("global",
						messageSource.getMessage("message.save.imports", null, LocaleContextHolder.getLocale())));
				gameForm.setGames(new ArrayList<Game>());
				return "/games/iGame";
			}

			String name = auth.getName();
			Account creator = accountService.findByUsername(name);

			String retorno = gameService.saveAll(gameForm.getGames(), creator);
			model.addAttribute("games", gameService.findAllByOrderByDtGame());
			return retorno;

		} catch (Exception e) {
			bindingResult.addError(new ObjectError("global",
					messageSource.getMessage("game.importFile.error", null, LocaleContextHolder.getLocale())));
		}

		model.addAttribute("games", gameService.findAllByOrderByDtGame());
		return "/games/iGame";

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
