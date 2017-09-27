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
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.Repository.ConferenceRepository;
import br.bfa.manager.Repository.TeamRepository;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Role;
import br.bfa.manager.entity.Team;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {
	private static final Logger logger = LoggerFactory.getLogger(TeamServiceImpl.class);

	private final TeamRepository teamRepository;
	
	private final ConferenceRepository conferenceRepository;

	private String response;

	public TeamServiceImpl(TeamRepository teamRepository, ConferenceRepository conferenceRepository) {
		this.teamRepository = teamRepository;
		this.conferenceRepository = conferenceRepository;
	}

	@Override
	public List<Team> findAllByStatus(Boolean status) {
		return teamRepository.findAllByStatus(status);
	}

	@Override
	public String save(String path, MultipartFile file, Team team) {
		try {
			team.setStatus(Boolean.TRUE);
			teamRepository.save(team);
			if (!file.isEmpty()) {
				team.setLogoPath(saveUploadedFile(path, file, team.getId().toString() + team.getName()));

				teamRepository.save(team);
			}
			logger.info("Team Saved");
			return "/teams/lTeam";
		} catch (Exception e) {

		}
		logger.info("Error Saving Team");
		return response;
	}

	@Override
	public String delete(Long id) {
		Team team = teamRepository.findById(id);
		team.setStatus(Boolean.FALSE);
		teamRepository.save(team);
		logger.info("Team Deleted");
		return response;
	}

	@Override
	public List<Team> findAllByStatusOrderByName(Boolean status) {
		return teamRepository.findAllByStatusOrderByName(status);
	}

	@Override
	public Team findById(Long id) {
		return teamRepository.findById(id);
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
		return File.separatorChar + "logos" + File.separatorChar + fileName + "." + extension;
	}

	@Override
	public Team findByEmail(String username) {
		return teamRepository.findByEmail(username);
	}

	@Override
	public ArrayList<Team> carregarXls(MultipartFile file) throws InvalidFormatException, IOException {
		Workbook exWorkBook = WorkbookFactory.create(file.getInputStream());
		Sheet worksheet = exWorkBook.getSheetAt(0);
		Iterator<Row> rowIterator = worksheet.iterator();
		rowIterator.next();
		ArrayList<Team> teams = new ArrayList<Team>();
		while (rowIterator.hasNext()) {
			try {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Team team = new Team();

					// Nome da Equipe
					Cell cell = cellIterator.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					team.setName(cell.getStringCellValue());

					// Razão Social
					cell = cellIterator.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					team.setLegalName(cell.getStringCellValue());
					
					// CNPJ
					cell = cellIterator.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					team.setLegalId(cell.getStringCellValue());
					
					// Nome do Presidente
					cell = cellIterator.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					team.setManager(cell.getStringCellValue());

					// Telefone do Presidente
					cell = cellIterator.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					team.setPhone(cell.getStringCellValue());

					// Facebook da Equipe
					cell = cellIterator.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					team.setFacebook(cell.getStringCellValue());

					// Twitter da Equipe
					cell = cellIterator.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					team.setTwitter(cell.getStringCellValue());

					// Instagram da Equipe
					cell = cellIterator.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					team.setInstagram(cell.getStringCellValue());

					// Site da Equipe
					cell = cellIterator.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					team.setSite(cell.getStringCellValue());

					// E-mail para contato
					cell = cellIterator.next();	
					cell.setCellType(Cell.CELL_TYPE_STRING);
					team.setEmail(cell.getStringCellValue());

					// Cidade da Equipe
					cell = cellIterator.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					team.setCity(cell.getStringCellValue());

					// Data de Fundação
					cell = cellIterator.next();
					team.setDtEstablishment(cell.getDateCellValue());
					
					
					// Conferencia
					cell = cellIterator.next();
					team.setConference(conferenceRepository.findById((long) cell.getNumericCellValue()));
					
					
					teams.add(team);
					
					
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		return teams;
	}

	@Override
	public String saveAll(List<Team> teams, Account creator) {
		for(Team team : teams){
			team.setDateCreate(new Date());
			team.setUserCreator(creator);
			team.setStatus(Boolean.TRUE);
			teamRepository.save(team);
			
		}
		return "/teams/lTeam";
	}

	@Override
	public List<Team> findAllByAuthorities(Account user) {
		List<Role> roles = user.getAuthorities();
		List<Team> teams = new ArrayList<Team>();
		for(Role role : roles){
			if(role.getName().contains("ADMIN")){
				return teamRepository.findAllByStatus(Boolean.TRUE);
			}else if(role.getName().contains("CONFERENCE")){
				teams = teamRepository.findAllByConference(user.getConference());
			}else if(role.getName().contains("TEAM")){
				teams.add(teamRepository.findByEmail(user.getUsername()));
			}
			
		}
		
		return teams;
	}

	@Override
	public Team findByAthletesEmailIn(String email) {
		return teamRepository.findByAthletesEmailIn(email);
	}

	@Override
	public List<Team> findAllTeamsWithImportsAtive() {
		return teamRepository.findAllTeamsWithImportsAtive();
	}

}
