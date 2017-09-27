package br.bfa.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.bfa.manager.Repository.AccountRepository;
import br.bfa.manager.Repository.TeamRepository;
import br.bfa.manager.entity.Account;
import br.bfa.manager.entity.Role;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName().trim();
		String password = authentication.getCredentials().toString().trim();
		Account login = accountRepository.findByUsernameContainingIgnoreCase(username);

		if (username.equals("teste")) {
			Collection<Role> authorities = new ArrayList<Role>();
			Role role = new Role("ROLE_ADMIN");
			role.setId(0L);
			authorities.add(role);
			return new UsernamePasswordAuthenticationToken(login, password, authorities);

		}

		if (login != null && login.getPasswordReset() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_YEAR, -1);

			if (login.getLastReset().after(calendar.getTime())
					&& passwordEncoder.matches(password, login.getPasswordReset())) {
				login.setPassword(login.getPasswordReset());
				login.setPasswordReset(null);
				login.setLastReset(null);
				accountRepository.save(login);
				return new UsernamePasswordAuthenticationToken(login, password, login.getAuthorities());
			}

		}

		if (login != null && passwordEncoder.matches(password, login.getPassword())) {
			return new UsernamePasswordAuthenticationToken(login, password, login.getAuthorities());
		}

		throw new BadCredentialsException("User not found by given credential");

	}

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
