package br.bfa.manager.Service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.entity.Conference;
import br.bfa.manager.entity.League;

/**
 * @author igor Furtado <igorfoliver@gmail.com>
 */
public interface ConferenceService {

    List<Conference> findAllByStatus(Boolean status);

    String delete(Long id);

	List<Conference> findAll();

	Conference findById(Long id);

	Conference save(Conference conference);

	void save(MultipartFile file, String path, Conference conference) throws IOException;

	List<Conference> findAllByLeague(League league);

}