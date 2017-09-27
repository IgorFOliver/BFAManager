package br.bfa.manager.Service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import br.bfa.manager.Repository.ConfigurationRepository;
import br.bfa.manager.Repository.ImportsRepository;
import br.bfa.manager.entity.Configuration;

@Service
@Transactional
public class ConfigurationServiceImpl implements ConfigurationService {

	private final ConfigurationRepository configurationRepository;


	public ConfigurationServiceImpl(ConfigurationRepository configurationRepository, ImportsRepository importsRepository) {
		this.configurationRepository = configurationRepository;
	}

	@Override
	public Configuration save(Configuration configuration) {
		return configurationRepository.save(configuration);
	}

	@Override
	public Configuration findFirstByOrderById() {
		return configurationRepository.findFirstByOrderById();
	}

}
