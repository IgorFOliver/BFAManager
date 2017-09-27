package br.bfa.manager.Service;

import java.io.IOException;
import java.util.List;

import br.bfa.manager.entity.Imports;

/**
 * @author igor Furtado <igorfoliver@gmail.com>
 */
public interface ImportsService {

	List<Imports> findAll();

	Imports findById(Long id);

	void save(Imports imports) throws IOException;

	List<Imports> findAllImportsAtive();

	void delete(Long id);
	
}

