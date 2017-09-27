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

import br.bfa.manager.Repository.ConferenceRepository;
import br.bfa.manager.entity.Conference;
import br.bfa.manager.entity.League;

@Service
@Transactional
public class ConferenceServiceImpl implements ConferenceService {
	private static final Logger logger = LoggerFactory.getLogger(ConferenceServiceImpl.class);

	private final ConferenceRepository conferenceRepository;

	private String response;

	public ConferenceServiceImpl(ConferenceRepository conferenceRepository) {
		this.conferenceRepository = conferenceRepository;
	}

	@Override
	public List<Conference> findAllByStatus(Boolean status) {
		return conferenceRepository.findAllByStatus(status);
	}


	@Override
	public String delete(Long id) {
		Conference conference = conferenceRepository.findById(id);
		conference.setStatus(Boolean.FALSE);
		conferenceRepository.save(conference);
		logger.info("Conference Deleted");
		return response;
	}

	@Override
	public List<Conference> findAll() {
		return conferenceRepository.findAll();
	}

	@Override
	public Conference findById(Long id) {
		return conferenceRepository.findById(id);
	}

	@Override
	public Conference save(Conference conference) {
		return conferenceRepository.save(conference);
	}

	@Override
	public void save(MultipartFile file, String path, Conference conference) throws IOException {
		conference.setStatus(Boolean.TRUE);
		conferenceRepository.save(conference);
		if (!file.isEmpty()) {
			conference.setLogo(saveUploadedFile(path, file, conference.getId().toString() + conference.getName()));

			conferenceRepository.save(conference);
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

	@Override
	public List<Conference> findAllByLeague(League league) {
		return conferenceRepository.findAllByLeague(league);
	}
}
