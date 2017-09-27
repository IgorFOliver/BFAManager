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

import javax.mail.internet.InternetAddress;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.Enum.BloodType;
import br.bfa.manager.Enum.ImportsEvaluation;
import br.bfa.manager.Enum.Position;
import br.bfa.manager.Generic.CPFValidator;
import br.bfa.manager.Repository.AthleteRepository;
import br.bfa.manager.Repository.ImportsRepository;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.Imports;
import br.bfa.manager.entity.Team;

@Service
@Transactional
public class AthleteServiceImpl implements AthleteService {
	private static final Logger logger = LoggerFactory.getLogger(AthleteServiceImpl.class);

	private final AthleteRepository athleteRepository;

	private final ImportsRepository importsRepository;

	private String response;

	public AthleteServiceImpl(AthleteRepository athleteRepository, ImportsRepository importsRepository) {
		this.athleteRepository = athleteRepository;
		this.importsRepository = importsRepository;
	}

	@Override
	public List<Athlete> findAllByStatus(Boolean status) {
		return athleteRepository.findAllByStatus(status);
	}

	@Override
	public String save(String path, MultipartFile file, Athlete athlete) {
		try {
			athlete.setStatus(Boolean.TRUE);
			if (athlete.getContractSigned() == null) {
				athlete.setContractSigned(Boolean.FALSE);
			}
			
			athleteRepository.save(athlete);
			
			
			if (athlete.getPlaceBirth() != null && !athlete.getPlaceBirth().toString().equals("pt_BR") && 
					!athlete.getPlaceBirth().toString().equalsIgnoreCase("none")) {
				Imports imports = importsRepository.findByAthleteId(athlete.getId());
				if (imports == null) {
					imports = new Imports();
				}
				imports.setAthlete(athlete);
				imports.setTeam(athlete.getTeam());
				imports.setEvaluationStatus(ImportsEvaluation.SUBMITED);
				importsRepository.save(imports);
			}

			if (!file.isEmpty()) {
				athlete.setProfilePic(saveUploadedFile(path, file, athlete.getId().toString() + athlete.getName()));

				athleteRepository.save(athlete);
			}
			logger.info("Athlete Saved");
			return "/athletes/lAthlete";
		} catch (Exception e) {

		}
		logger.info("Error Saving Athlete");
		return response;
	}

	@Override
	public String delete(Long id) {
		Athlete athlete = athleteRepository.findById(id);
		athlete.setStatus(Boolean.FALSE);
		athleteRepository.save(athlete);
		logger.info("Athlete Deleted");
		return response;
	}

	@Override
	public List<Athlete> findAllByStatusAndTeamOrderByName(Boolean status, Team team) {
		return athleteRepository.findAllByStatusAndTeamOrderByName(status, team);
	}

	@Override
	public Athlete findById(Long id) {
		return athleteRepository.findById(id);
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
		return File.separatorChar + "profilePics" + File.separatorChar + fileName + "." + extension;
	}

	@Override
	public Athlete findByEmail(String username) {
		return athleteRepository.findByEmail(username);
	}

	@Override
	public ArrayList<Athlete> carregarXls(MultipartFile file, BindingResult bindingResult)
			throws InvalidFormatException, IOException {
		Workbook exWorkBook = WorkbookFactory.create(file.getInputStream());
		Sheet worksheet = exWorkBook.getSheetAt(0);
		Iterator<Row> rowIterator = worksheet.iterator();
		rowIterator.next();
		ArrayList<Athlete> athletes = new ArrayList<Athlete>();
		while (rowIterator.hasNext()) {
			Athlete athlete = new Athlete();

			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();

			if (row.getLastCellNum() <= 8) {
				break;
			}
			Athlete nAthlete = new Athlete();
			athlete.setStatus(Boolean.TRUE);

			// Nome do Atleta
			Cell cell = cellIterator.next();

			try {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				athlete.setName(cell.getStringCellValue());

				if (athlete.getName() == null || athlete.getName().isEmpty()) {
					bindingResult.addError(new ObjectError("name", new String[] { "error.name.required" },
							new String[] { athlete.getName() }, "error.name.required"));
				}

				// RG
				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				athlete.setLegalId2(cell.getStringCellValue());

				if (athlete.getLegalId2() == null || athlete.getLegalId2().isEmpty()) {
					bindingResult.addError(new ObjectError("legalId2", new String[] { "error.legalId.required" },
							new String[] { athlete.getName() }, "error.legalId2.required"));

				} else {
					nAthlete = athleteRepository.findByLegalId2(athlete.getLegalId2());
					if (nAthlete != null) {
						bindingResult.addError(new ObjectError("legalId2", new String[] { "error.legalId.unique" },
								new String[] { athlete.getName() }, "error.legalId2.unique"));
					}
				}

				// CPF
				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				athlete.setLegalId(cell.getStringCellValue());

				if (athlete.getLegalId() == null) {
					bindingResult.addError(new ObjectError("legalId", new String[] { "error.legalId.required" },
							new String[] { athlete.getName() }, "error.legalId.required"));
				} else {
					nAthlete = athleteRepository.findByLegalId(athlete.getLegalId());
					if (nAthlete != null) {
						bindingResult.addError(new ObjectError("legalId", new String[] { "error.legalId.unique" },
								new String[] { athlete.getLegalId() }, "error.legalId.invalid"));
						athlete.setStatus(Boolean.FALSE);
					}
					if (!CPFValidator.isValidCPF(athlete.getLegalId().replaceAll("\\D+", ""))) {
						bindingResult.addError(new ObjectError("legalId", new String[] { "error.legalId.invalid" },
								new String[] { athlete.getLegalId() }, "error.legalId.invalid"));
					}
				}

				// Telefone
				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				athlete.setPhone(cell.getStringCellValue());

				// Endereço
				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				StringBuilder adress = new StringBuilder(cell.getStringCellValue());

				cell = cellIterator.next();
				adress.append(" - ");
				cell.setCellType(Cell.CELL_TYPE_STRING);
				adress.append(cell.getStringCellValue());

				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				adress.append(" - ");
				adress.append(cell.getStringCellValue());

				athlete.setAdress(adress.toString());

				// Email
				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				athlete.setEmail(cell.getStringCellValue().toLowerCase().replaceAll("[^.@A-z0-9._]", ""));
				if (athlete.getEmail() == null || athlete.getEmail().isEmpty()) {
					bindingResult.addError(new ObjectError("Email", new String[] { "error.email.required" },
							new String[] { athlete.getName() }, "error.email.required"));
					athlete.setStatus(Boolean.FALSE);
				} else {

					nAthlete = athleteRepository.findByEmail(athlete.getEmail());
					if (nAthlete != null) {
						bindingResult.addError(new ObjectError("Email", new String[] { "error.email.unique" },
								new String[] { athlete.getEmail() }, "error.email.unique"));
						athlete.setStatus(Boolean.FALSE);
					}
					InternetAddress emailAddr = new InternetAddress(athlete.getEmail());
					emailAddr.validate();
				}

			} catch (Exception e) {
				bindingResult.addError(new ObjectError("account", new String[] { "error.loadInfos" },
						new String[] { athlete.getName() }, "error.loadInfos"));
				athlete.setStatus(Boolean.FALSE);
			}

			try {
				// Peso
				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				athlete.setWeight(cell.getStringCellValue());
			} catch (Exception e) {
			}

			try {

				// Altura
				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				athlete.setHeight(cell.getStringCellValue());
			} catch (Exception e) {
			}
			try {
				// Posições
				cell = cellIterator.next();
				athlete.setPositions(new ArrayList<Position>());
				athlete.getPositions().add(Position.obterTipoPorCodigo(cell.getStringCellValue()));
			} catch (Exception e) {
			}

			try {
				cell = cellIterator.next();
				if (cell.getStringCellValue() != null && !cell.getStringCellValue().equals("N/A")) {
					athlete.getPositions().add(Position.obterTipoPorCodigo(cell.getStringCellValue()));
				}
			} catch (Exception e) {
			}
			try {
				// Joga desde
				cell = cellIterator.next();
				athlete.setDtStart(cell.getDateCellValue());
			} catch (Exception e) {
			}
			try {
				// Joga desde
				cell = cellIterator.next();
				athlete.setBloodType(BloodType.obterTipoPorCodigo(cell.getStringCellValue()));
			} catch (Exception e) {
			}
			try {
				// Joga desde
				cell = cellIterator.next();
				athlete.setDtBirth(cell.getDateCellValue());
			} catch (Exception e) {
			}

			Boolean add = true;
			for (Athlete listAthlete : athletes) {
				if ((athlete.getEmail() != null && listAthlete.getEmail().equals(athlete.getEmail()))
						|| (athlete.getLegalId() != null && listAthlete.getLegalId().equals(athlete.getLegalId()))
						|| (athlete.getLegalId2() != null && listAthlete.getLegalId2().equals(athlete.getLegalId2()))) {
					bindingResult.addError(new ObjectError("Infos", new String[] { "error.athlete.import.duplicated" },
							new String[] { athlete.getName() }, "error.athlete.import.duplicated"));
					athlete.setStatus(Boolean.FALSE);
					break;
				}
			}

			if (add) {
				athletes.add(athlete);

			}

		}

		return athletes;
	}

	@Override
	public String saveAll(List<Athlete> athletes, Account creator, Team team) {
		for (Athlete athlete : athletes) {
			if (athlete.getStatus()) {
				athlete.setContractSigned(Boolean.FALSE);
				athlete.setDateCreate(new Date());
				athlete.setUserCreator(creator);
				athlete.setStatus(Boolean.TRUE);
				athlete.setTeam(team);
				athleteRepository.save(athlete);
			}

		}
		return "/athletes/lAthlete";
	}

	@Override
	public Athlete findByLegalId(String legalId) {
		return athleteRepository.findByLegalId(legalId);
	}

	@Override
	public Athlete findByLegalId2(String legalId2) {
		return athleteRepository.findByLegalId2(legalId2);
	}

	@Override
	public Athlete save(Athlete athlete) {
		return athleteRepository.save(athlete);
	}

	@Override
	public Integer countByContractSignedTrue() {
		return athleteRepository.countByContractSignedTrue();
	}

	@Override
	public Integer countByStatusTrue() {
		return athleteRepository.countByStatusTrue();
	}

	@Override
	public Integer countByConference(Long id) {
		return athleteRepository.countByConference(id);
	}

	@Override
	public List<Athlete> findAllAthletesAtiveSigned() {
		return athleteRepository.findAllAthletesAtiveSigned();
	}

	@Override
	public List<Athlete> findAllAthletesAtiveSignedByTeam(Team team) {
		return athleteRepository.findAllAthletesAtiveSignedByTeam(team);
	}


}
