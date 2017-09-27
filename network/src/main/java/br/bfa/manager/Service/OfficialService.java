package br.bfa.manager.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Official;

/**
 * @author igor Furtado <igorfoliver@gmail.com>
 */
public interface OfficialService {

    /**
     * Retrieves all Officials from database
     *
     * @return
     */
    List<Official> findAllByStatus(Boolean status);

    /**
     * Adds a new Official to the database
     * @param path 
     * @param file 
     *
     * @param Official
     * @return
     */
    String save(String path, MultipartFile file, Official Official);

    /**
     * Deletes a Official by Id from database
     *
     * @param id
     * @return
     */
    String delete(Long id);

	List<Official> findAllByStatusOrderByName(Boolean status);

	Official findById(Long id);

	Official findByEmail(String username);

	ArrayList<Official> carregarXls(MultipartFile file, BindingResult bindingResult) throws InvalidFormatException, IOException;

	String saveAll(List<Official> officials, Account creator);

	Official findByLegalId(String legalId);

	Official findByLegalId2(String legalId2);

	Official save(Official athlete);


}