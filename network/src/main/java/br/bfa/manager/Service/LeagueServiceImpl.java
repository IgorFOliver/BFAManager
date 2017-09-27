package br.bfa.manager.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.Repository.LeagueRepository;
import br.bfa.manager.entity.League;

@Service
@Transactional
public class LeagueServiceImpl implements LeagueService {
	private static final Logger logger = LoggerFactory.getLogger(LeagueServiceImpl.class);

	private final LeagueRepository leagueRepository;

	private String response;

	public LeagueServiceImpl(LeagueRepository leagueRepository) {
		this.leagueRepository = leagueRepository;
	}

	@Override
	public List<League> findAllByStatus(Boolean status) {
		return leagueRepository.findAllByStatus(status);
	}


	@Override
	public String delete(Long id) {
		League league = leagueRepository.findById(id);
		league.setStatus(Boolean.FALSE);
		leagueRepository.save(league);
		logger.info("League Deleted");
		return response;
	}

	@Override
	public List<League> findAll() {
		return leagueRepository.findAll();
	}

	@Override
	public League findById(Long id) {
		return leagueRepository.findById(id);
	}

	@Override
	public League save(League league) {
		return leagueRepository.save(league);
	}

	@Override
	public void save(MultipartFile file, String path, League league) throws IOException {
		league.setStatus(Boolean.TRUE);
		leagueRepository.save(league);
		if (!file.isEmpty()) {
			league.setLogo(saveUploadedFile(path, file, league.getId().toString() + league.getName()));

			leagueRepository.save(league);
		}
		logger.info("Athlete Saved");
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
}
