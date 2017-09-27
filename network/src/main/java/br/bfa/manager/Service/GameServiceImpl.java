package br.bfa.manager.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.Repository.AthleteGameRepository;
import br.bfa.manager.Repository.AthleteRepository;
import br.bfa.manager.Repository.ConferenceRepository;
import br.bfa.manager.Repository.GameRepository;
import br.bfa.manager.Repository.TeamRepository;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.AthleteGame;
import br.bfa.manager.entity.Game;
import br.bfa.manager.entity.Team;

@Service
@Transactional
public class GameServiceImpl implements GameService {
	private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

	private final GameRepository gameRepository;

	private final TeamRepository teamRepository;
	
	private final AthleteGameRepository athleteGameRepository;

	private final ConferenceRepository conferenceRepository;

	public GameServiceImpl(GameRepository gameRepository, TeamRepository teamRepository,
			ConferenceRepository conferenceRepository, AthleteRepository athleteRepository, AthleteGameRepository athleteGameRepository) {
		this.gameRepository = gameRepository;
		this.teamRepository = teamRepository;
		this.conferenceRepository = conferenceRepository;
		this.athleteGameRepository = athleteGameRepository;
	}

	@Override
	public List<Game> findAll() {
		return gameRepository.findAll();
	}

	@Override
	public String delete(Long id) {
		gameRepository.delete(id);
		logger.info("Game Deleted");
		return "";
	}

	@Override
	public Game findById(Long id) {
		return gameRepository.findById(id);
	}

	@Override
	public ArrayList<Game> carregarXls(MultipartFile file, BindingResult bindingResult)
			throws InvalidFormatException, IOException {
		Workbook exWorkBook = WorkbookFactory.create(file.getInputStream());
		Sheet worksheet = exWorkBook.getSheetAt(0);
		Iterator<Row> rowIterator = worksheet.iterator();
		ArrayList<Game> games = new ArrayList<Game>();
		while (rowIterator.hasNext()) {
			try {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				Game nGame = new Game();

				Cell cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				nGame.setHomeTeam(teamRepository.findById(new Long(cell.getStringCellValue())));

				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				nGame.setAwayTeam(teamRepository.findById(new Long(cell.getStringCellValue())));

				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				nGame.setConference(conferenceRepository.findById(new Long(cell.getStringCellValue())));

				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				nGame.setHomeColor(cell.getStringCellValue());

				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				nGame.setAwayColor(cell.getStringCellValue());

				cell = cellIterator.next();
				nGame.setDtGame(cell.getDateCellValue());

				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				nGame.setPlace(cell.getStringCellValue());

				games.add(nGame);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return games;
	}

	@Override
	public String saveAll(List<Game> games, Account creator) {
		for (Game game : games) {
			game.setDateCreate(new Date());
			game.setAwayTeam(teamRepository.findById(game.getAwayTeam().getId()));
			game.setHomeTeam(teamRepository.findById(game.getHomeTeam().getId()));
			game.setConference(conferenceRepository.findById(game.getConference().getId()));
			game.setUserCreator(creator);
			gameRepository.save(game);
		}
		return "/games/lGame";
	}

	@Override
	public List<Athlete> findAllByGameAndTeam(Game game, Team team) {
		return null; //athleteRepository.findAllByGamesAndTeamOrderByName(game, team);
	}

	@Override
	public List<Game> findAllByOrderByDtGame() {
		return gameRepository.findAllByOrderByDtGameAsc();
	}

	@Override
	public Game save(String path, MultipartFile file, Game game) throws IOException {
		List<AthleteGame> athletesPlayed = new ArrayList<AthleteGame>();
		Game gameSaved = gameRepository.save(game);
		
		for(AthleteGame aGame : game.getHomeAthletesGame()){
			Boolean played = aGame.getPlayed();
			if(aGame.getId() != null && aGame.getId() != 0){
				aGame = athleteGameRepository.findById(aGame.getId());
				
			}else{
				aGame.setHomeGame(gameSaved);
				aGame.setTeam(gameSaved.getHomeTeam());
			}
			
			aGame.setPlayed(played);
			athletesPlayed.add(aGame);
		}
		athleteGameRepository.save(athletesPlayed);
		gameSaved.setHomeAthletesGame(athletesPlayed);
		
		athletesPlayed = new ArrayList<AthleteGame>();
		
		for(AthleteGame aGame : game.getAwayAthletesGame()){
			Boolean played = aGame.getPlayed();
			if(aGame.getId() != null && aGame.getId() != 0){
				aGame = athleteGameRepository.findById(aGame.getId());
				
			}else{
				aGame.setAwayGame(gameSaved);
				aGame.setTeam(gameSaved.getAwayTeam());
			}
			
			aGame.setPlayed(played);
			athletesPlayed.add(aGame);
		}
		gameSaved.setAwayAthletesGame(athletesPlayed);
		athleteGameRepository.save(athletesPlayed);
		
		if (!file.isEmpty()) {
			gameSaved.setGameDocs(saveUploadedFile(path, file,
					gameSaved.getHomeTeam().getName() + " vs " + gameSaved.getAwayTeam().getName()));
			gameRepository.save(gameSaved);
		}

		return gameSaved;
	}

	public static String saveUploadedFile(String path, MultipartFile uploadedFile, String fileName) throws IOException {
		// First, Generate file to make directories
		String extension = uploadedFile.getOriginalFilename().split("\\.")[1];
		String savedFileName = path + File.separatorChar + fileName + "." + extension;
		File fileToSave = new File(savedFileName);
		fileToSave.getParentFile().mkdirs();
		fileToSave.delete();
		// Generate path file to copy file
		Path folder = Paths.get(savedFileName);
		Path fileToSavePath = Files.createFile(folder);
		// Copy file to server
		InputStream input = uploadedFile.getInputStream();
		Files.copy(input, fileToSavePath, StandardCopyOption.REPLACE_EXISTING);
		return File.separatorChar + "gamesDocs" + File.separatorChar + fileName + "." + extension;
	}

	@Override
	public List<Game> findAllByTeamByOrderByDtGame(Team team) {
		return gameRepository.findAllByTeamByOrderByDtGame(team);
	}

}
