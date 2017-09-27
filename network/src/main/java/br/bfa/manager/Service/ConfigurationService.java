package br.bfa.manager.Service;

import br.bfa.manager.entity.Configuration;

/**
 * @author igor Furtado <igorfoliver@gmail.com>
 */
public interface ConfigurationService {


	Configuration save(Configuration configuration);

	Configuration findFirstByOrderById();


}