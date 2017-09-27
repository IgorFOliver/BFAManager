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
import br.bfa.manager.Enum.PositionOfficial;
import br.bfa.manager.Generic.CPFValidator;
import br.bfa.manager.Repository.ImportsRepository;
import br.bfa.manager.Repository.OfficialRepository;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Official;

@Service
@Transactional
public class OfficialServiceImpl implements OfficialService {
	private static final Logger logger = LoggerFactory.getLogger(OfficialServiceImpl.class);

	private final OfficialRepository officialRepository;
	
	private String response;

	public OfficialServiceImpl(OfficialRepository officialRepository, ImportsRepository importsRepository) {
		this.officialRepository = officialRepository;
	}

	@Override
	public List<Official> findAllByStatus(Boolean status) {
		return officialRepository.findAllByStatus(status);
	}

	@Override
	public String save(String path, MultipartFile file, Official official) {
		try {
			official.setStatus(Boolean.TRUE);
			officialRepository.save(official);

			if (!file.isEmpty()) {
				official.setProfilePic(saveUploadedFile(path, file, official.getId().toString() + official.getName()));

				officialRepository.save(official);
			}
			logger.info("Official Saved");
			return "/officials/lOfficial";
		} catch (Exception e) {

		}
		logger.info("Error Saving Official");
		return response;
	}

	@Override
	public String delete(Long id) {
		Official official = officialRepository.findById(id);
		official.setStatus(Boolean.FALSE);
		officialRepository.save(official);
		logger.info("Official Deleted");
		return response;
	}

	@Override
	public Official findById(Long id) {
		return officialRepository.findById(id);
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
	public Official findByEmail(String username) {
		return officialRepository.findByEmail(username);
	}

	@Override
	public ArrayList<Official> carregarXls(MultipartFile file, BindingResult bindingResult)
			throws InvalidFormatException, IOException {
		Workbook exWorkBook = WorkbookFactory.create(file.getInputStream());
		Sheet worksheet = exWorkBook.getSheetAt(0);
		Iterator<Row> rowIterator = worksheet.iterator();
		rowIterator.next();
		ArrayList<Official> officials = new ArrayList<Official>();
		while (rowIterator.hasNext()) {
			Official official = new Official();

			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();

			if (row.getLastCellNum() <= 8) {
				break;
			}
			Official nOfficial = new Official();
			official.setStatus(Boolean.TRUE);

			// Nome do Atleta
			Cell cell = cellIterator.next();

			try {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				official.setName(cell.getStringCellValue());

				if (official.getName() == null || official.getName().isEmpty()) {
					bindingResult.addError(new ObjectError("name", new String[] { "error.name.required" },
							new String[] { official.getName() }, "error.name.required"));
				}

				// RG
				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				official.setLegalId2(cell.getStringCellValue());

				if (official.getLegalId2() == null) {
					bindingResult.addError(new ObjectError("legalId2", new String[] { "error.legalId.required" },
							new String[] { official.getName() }, "error.legalId2.required"));

				} else {
					nOfficial = officialRepository.findByLegalId2(official.getLegalId2());
					if (nOfficial != null) {
						bindingResult.addError(new ObjectError("legalId2", new String[] { "error.legalId.unique" },
								new String[] { official.getName() }, "error.legalId2.unique"));
					}
				}

				// CPF
				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				official.setLegalId(cell.getStringCellValue());

				if (official.getLegalId() == null) {
					bindingResult.addError(new ObjectError("legalId", new String[] { "error.legalId.required" },
							new String[] { official.getName() }, "error.legalId.required"));
				} else {
					nOfficial = officialRepository.findByLegalId(official.getLegalId());
					if (nOfficial != null) {
						bindingResult.addError(new ObjectError("legalId", new String[] { "error.legalId.unique" },
								new String[] { official.getLegalId() }, "error.legalId.invalid"));
						official.setStatus(Boolean.FALSE);
					}
					if (!CPFValidator.isValidCPF(official.getLegalId().replaceAll("\\D+", ""))) {
						bindingResult.addError(new ObjectError("legalId", new String[] { "error.legalId.invalid" },
								new String[] { official.getLegalId() }, "error.legalId.invalid"));
					}
				}

				// Telefone
				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				official.setPhone(cell.getStringCellValue());

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

				official.setAdress(adress.toString());

				// Email
				cell = cellIterator.next();
				cell.setCellType(Cell.CELL_TYPE_STRING);
				official.setEmail(cell.getStringCellValue().toLowerCase().replaceAll("[^.@A-z0-9._]", ""));
				if (official.getEmail() == null || official.getEmail().isEmpty()) {
					bindingResult.addError(new ObjectError("Email", new String[] { "error.email.required" },
							new String[] { official.getName() }, "error.email.required"));
					official.setStatus(Boolean.FALSE);
				} else {

					nOfficial = officialRepository.findByEmail(official.getEmail());
					if (nOfficial != null) {
						bindingResult.addError(new ObjectError("Email", new String[] { "error.email.unique" },
								new String[] { official.getEmail() }, "error.email.unique"));
						official.setStatus(Boolean.FALSE);
					}
					InternetAddress emailAddr = new InternetAddress(official.getEmail());
					emailAddr.validate();
				}

			} catch (Exception e) {
				bindingResult.addError(new ObjectError("account", new String[] { "error.loadInfos" },
						new String[] { official.getName() }, "error.loadInfos"));
				official.setStatus(Boolean.FALSE);
			}

			try {
				// Posições
				cell = cellIterator.next();
				official.setPositions(new ArrayList<PositionOfficial>());
				official.getPositions().add(PositionOfficial.obterTipoPorCodigo(cell.getStringCellValue()));
			} catch (Exception e) {
			}

			try {
				cell = cellIterator.next();
				if (cell.getStringCellValue() != null && !cell.getStringCellValue().equals("N/A")) {
					official.getPositions().add(PositionOfficial.obterTipoPorCodigo(cell.getStringCellValue()));
				}
			} catch (Exception e) {
			}
			try {
				// apita desde
				cell = cellIterator.next();
				official.setDtStart(cell.getDateCellValue());
			} catch (Exception e) {
			}
			try {
				// tipo sanguineo
				cell = cellIterator.next();
				official.setBloodType(BloodType.obterTipoPorCodigo(cell.getStringCellValue()));
			} catch (Exception e) {
			}
			try {
				// data nascimento
				cell = cellIterator.next();
				official.setDtBirth(cell.getDateCellValue());
			} catch (Exception e) {
			}

			Boolean add = true;
			for (Official listOfficial : officials) {
				if ((official.getEmail() != null && listOfficial.getEmail().equals(official.getEmail()))
						|| (official.getLegalId() != null && listOfficial.getLegalId().equals(official.getLegalId()))
						|| (official.getLegalId2() != null && listOfficial.getLegalId2().equals(official.getLegalId2()))) {
					bindingResult.addError(new ObjectError("Infos", new String[] { "error.official.import.duplicated" },
							new String[] { official.getName() }, "error.official.import.duplicated"));
					official.setStatus(Boolean.FALSE);
					break;
				}
			}

			if (add) {
				officials.add(official);

			}

		}

		return officials;
	}

	@Override
	public String saveAll(List<Official> officials, Account creator) {
		for (Official official : officials) {
			if (official.getStatus()) {
				official.setDateCreate(new Date());
				official.setUserCreator(creator);
				official.setStatus(Boolean.TRUE);
				officialRepository.save(official);
			}

		}
		return "/officials/lOfficial";
	}

	@Override
	public Official findByLegalId(String legalId) {
		return officialRepository.findByLegalId(legalId);
	}

	@Override
	public Official findByLegalId2(String legalId2) {
		return officialRepository.findByLegalId2(legalId2);
	}

	@Override
	public Official save(Official official) {
		return officialRepository.save(official);
	}

	@Override
	public List<Official> findAllByStatusOrderByName(Boolean status) {
		return officialRepository.findAllByStatus(status);
	}

}
