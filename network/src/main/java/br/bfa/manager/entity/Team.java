package br.bfa.manager.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
 
@Entity
@EnableJpaAuditing
public class Team {
 
	public Team(String name) {
		this.name = name;
	}
    
    public Team(){}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Getter @Setter
	private Long id;
 
	@NotNull
	@Size(min=5, max=50)
    @Getter @Setter
    private String name;
    
    @Getter @Setter
    private String legalName;
    
    @Getter @Setter
    private String legalId;
    
    @Column(length = 5000)
    @Getter @Setter
    private String history;
    
    @Column(length = 5000)
    @Getter @Setter
    private String mediaGuide;
    
    @NotNull
    @Size(min=1, max=40)
    @Getter @Setter
    private String manager;
    
    @Getter @Setter
    private String phone;
    
    @Getter @Setter
    private String facebook;
    
    @Getter @Setter
    private String twitter;
    
    @Getter @Setter
    private String instagram;
    
    @NotNull
    @Getter @Setter
    private String email;
    
    @Getter @Setter
    private String state;
    
    @Getter @Setter
    private String site;
    
    @NotNull
    @Size(min=1, max=40)
    @Getter @Setter
    private String city;
    
    @Getter @Setter
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dtEstablishment;
    
    @Getter @Setter
    private String logoPath;
    
    @Getter @Setter
    private Boolean status;
    
    @Getter @Setter
    @OneToOne
    private Conference conference;
    
    @CreatedBy
    @Getter @Setter
    @OneToOne
    private Account userCreator;
    
    @CreatedDate
    @Getter @Setter
    private Date dateCreate;
    
    @OneToMany(mappedBy="team")
    @Getter @Setter
    private List<Athlete> athletes;
 
}