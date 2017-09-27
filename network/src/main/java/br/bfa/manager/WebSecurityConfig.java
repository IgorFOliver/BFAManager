package br.bfa.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().mvcMatchers("/accounts/nAccount").hasRole("ADMIN").mvcMatchers("/accounts/")
				.hasRole("ADMIN").mvcMatchers("/accounts/delete/**").hasRole("ADMIN").mvcMatchers("/accounts/save")
				.hasRole("ADMIN").mvcMatchers("/athletes/").hasRole("TEAM").mvcMatchers("/athletes/view")
				.hasRole("ATHLETE").mvcMatchers("/athletes/view/**").hasRole("TEAM").mvcMatchers("/athletes/delete/**")
				.hasRole("TEAM").mvcMatchers("/athletes/nAthlete").hasRole("TEAM").mvcMatchers("/teams/")
				.hasAnyRole("ADMIN", "CONFERENCE", "LEAGUE").mvcMatchers("/teams")
				.hasAnyRole("ADMIN").mvcMatchers("/athletes")
				.hasAnyRole("ADMIN").mvcMatchers("/accounts")
				.hasAnyRole("ADMIN").mvcMatchers("/teams/nTeam")
				.hasAnyRole("ADMIN", "CONFERENCE", "LEAGUE").mvcMatchers("/teams/view").hasAnyRole("TEAM", "ATHLETE")
				.mvcMatchers("/teams/delete/**").hasAnyRole("ADMIN", "CONFERENCE", "LEAGUE");

		http.authorizeRequests()
				.antMatchers("/javascripts/**", "/stylesheets/**", "/fonts/**", "/images/**",
						"/accounts/recoverAccount", "/accounts/recoverPassword", "/accounts/register", "**/favicon.ico",
						"/accounts/registerAccount")
				.permitAll().anyRequest().permitAll().anyRequest().authenticated();

		http.formLogin().loginPage("/login").defaultSuccessUrl("/", true).failureUrl("/login?error")
				.usernameParameter("username").permitAll();
		http.exceptionHandling().accessDeniedPage("/500");

		http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login");

	}

	@Autowired
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("teste").password("123").roles("USER");
		auth.authenticationProvider(this.customAuthenticationProvider);
	}

}
