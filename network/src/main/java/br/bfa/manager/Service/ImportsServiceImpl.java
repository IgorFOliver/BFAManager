package br.bfa.manager.Service;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.bfa.manager.Repository.ImportsRepository;
import br.bfa.manager.entity.Imports;

@Service
@Transactional
public class ImportsServiceImpl implements ImportsService {

	private static final Logger logger = LoggerFactory.getLogger(ImportsServiceImpl.class);

	private final ImportsRepository importsRepository;

	public ImportsServiceImpl(ImportsRepository importsRepository) {
		this.importsRepository = importsRepository;
	}

	@Override
	public List<Imports> findAll() {
		return importsRepository.findAll();
	}

	@Override
	public Imports findById(Long id) {
		return importsRepository.findById(id);
	}
	
	@Override
	public void save(Imports imports) throws IOException {
		importsRepository.save(imports);
		logger.info("Imports Saved");
	}

	@Override
	public List<Imports> findAllImportsAtive() {
		return importsRepository.findAllAtive();
	}

	@Override
	public void delete(Long id) {
		importsRepository.delete(id);
		
	}
	
}
