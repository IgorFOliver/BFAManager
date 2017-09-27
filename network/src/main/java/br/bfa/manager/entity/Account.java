package br.bfa.manager.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.userdetails.UserDetails;

import br.bfa.manager.Enum.Conference;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "account")
public class Account implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	@Setter
	private Long id;

	public Account(){};
	
	public Account(String username, String password, List<Role> authorities) {
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	@NotNull
	@Size(min = 1, max = 60)
	@Getter
	@Setter
	private String name;

	@NotNull
	@Email
	@Size(min = 1, max = 300)
	@Column(unique = true)
	@Getter
	@Setter
	private String username;

	@NotNull
	@Size(min = 7, max = 300)
	@Getter
	@Setter
	private String password;

	@Getter
	@Setter
	private String passwordConfirm;
	
	@Getter
	@Setter
	private String passwordReset;
	
	@Getter
	@Setter
	private Date lastReset;

	@Getter
	@Setter
	private Boolean status;

	@Getter 
	@Setter
	@Enumerated
	private Conference conference;
	
	@Getter
	@Setter
	@Cascade({CascadeType.ALL})
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private List<Role> authorities;

	@Getter
	@Setter
	private boolean accountNonExpired = true;

	@Getter
	@Setter
	private boolean accountNonLocked = true;

	@Getter
	@Setter
	private boolean credentialsNonExpired = true;

	@Getter
	@Setter
	private boolean enabled = true;

}