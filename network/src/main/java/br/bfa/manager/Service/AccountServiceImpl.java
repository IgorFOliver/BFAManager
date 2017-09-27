package br.bfa.manager.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.bfa.manager.Repository.AccountRepository;
import br.bfa.manager.Repository.AthleteRepository;
import br.bfa.manager.Repository.TeamRepository;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Athlete;
import br.bfa.manager.entity.Role;
import br.bfa.manager.entity.Team;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
	private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	private final AccountRepository accountRepository;

	private final TeamRepository teamRepository;

	private final AthleteRepository athleteRepository;

	private final EmailService emailService;

	private String response;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public AccountServiceImpl(AccountRepository accountRepository, TeamRepository teamRepository,
			AthleteRepository athleteRepository, EmailService emailService) {
		this.accountRepository = accountRepository;
		this.teamRepository = teamRepository;
		this.athleteRepository = athleteRepository;
		this.emailService = emailService;
	}

	@Override
	public List<Account> findAllByStatus(Boolean status) {
		return accountRepository.findAllByStatus(status);
	}

	@Override
	public Account save(Account account) {
		try {
			Team team = teamRepository.findByEmail(account.getUsername());
			Athlete athlete = athleteRepository.findByEmail(account.getUsername());
			account.setAuthorities(new ArrayList<Role>());
			account.setPassword(passwordEncoder.encode(account.getPassword()));
			account.setPasswordConfirm("");
			if (team != null) {
				Role role = new Role("ROLE_TEAM");
				account.getAuthorities().add(role);
			}
			if (athlete != null) {
				Role role = new Role("ROLE_ATHLETE");
				account.getAuthorities().add(role);
			}

			account.setStatus(Boolean.TRUE);
			accountRepository.save(account);
			logger.info("Account Saved");
			return account;
		} catch (Exception e) {

		}
		logger.info("Error Saving Account");
		return account;
	}

	@Override
	public String delete(Long id) {
		Account account = accountRepository.findById(id);
		account.setStatus(Boolean.FALSE);
		accountRepository.save(account);
		logger.info("Account Deleted");
		return response;
	}

	@Override
	public List<Account> findAllByStatusOrderByName(Boolean status) {
		return accountRepository.findAllByStatusOrderByName(status);
	}

	@Override
	public Account findById(Long id) {
		return accountRepository.findById(id);
	}

	@Override
	public Account findByUsername(String username) {
		return accountRepository.findByUsernameContainingIgnoreCase(username);
	}

	@Override
	public void recoverPassword(Account account) throws MessagingException {
		String newPassword = generatePassword();
		account.setPasswordReset(passwordEncoder.encode(newPassword));
		account.setLastReset(new Date());
		emailService.sendSimpleMailRecoverPassword(account.getUsername(), newPassword, new Locale("pt", "br"));
		accountRepository.save(account);
	}

	private String generatePassword() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(24, random).toString(10);
	}

}
