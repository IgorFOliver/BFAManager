package br.bfa.manager.Service;

import java.util.List;

import javax.mail.MessagingException;

import br.bfa.manager.entity.Account;

/**
 * @author igor Furtado <igorfoliver@gmail.com>
 */
public interface AccountService {

    /**
     * Retrieves all Accounts from database
     *
     * @return
     */
    List<Account> findAllByStatus(Boolean status);

    /**
     * Adds a new Account to the database
     * @param path 
     * @param file 
     *
     * @param Account
     * @return
     */
    Account save(Account Account);

    /**
     * Deletes a Account by Id from database
     *
     * @param id
     * @return
     */
    String delete(Long id);

	List<Account> findAllByStatusOrderByName(Boolean true1);

	Account findById(Long id);

	Account findByUsername(String username);

	void recoverPassword(Account account) throws MessagingException;

}