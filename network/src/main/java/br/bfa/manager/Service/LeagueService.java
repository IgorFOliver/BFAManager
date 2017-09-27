package br.bfa.manager.Service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.entity.League;

/**
 * @author igor Furtado <igorfoliver@gmail.com>
 */
public interface LeagueService {

    List<League> findAllByStatus(Boolean status);

    String delete(Long id);

	List<League> findAll();

	League findById(Long id);

	League save(League league);

	void save(MultipartFile file, String path, League league) throws IOException;

}