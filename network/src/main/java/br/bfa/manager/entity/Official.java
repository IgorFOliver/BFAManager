package br.bfa.manager.entity;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.format.annotation.DateTimeFormat;

import br.bfa.manager.Enum.BloodType;
import br.bfa.manager.Enum.PositionOfficial;
import lombok.Getter;
import lombok.Setter;
 
@Entity
@EnableJpaAuditing
public class Official {
 
	public Official(String name) {
		this.name = name;
	}
    
    public Official(){}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Getter @Setter
	private Long id;
	
	@NotNull
	@Size(min=1, max=150)
    @Getter @Setter
    private String name;
    
	@NotNull
	@Size(min=11)
	@Column(unique = true)
    @Getter @Setter
    private String legalId;
	
	@NotNull
	@Size(min=5)
	@Column(unique = true)
    @Getter @Setter
    private String legalId2;
    
    @Getter @Setter
    private String history;
    
    @Getter @Setter
    private String adress;
    
    @Getter @Setter
    private String phone;
    
    @Getter @Setter
    private String facebook;
    
    @Getter @Setter
    private String height;
    
    @Getter @Setter
    private String weight;
    
    @Getter @Setter
    private String twitter;
    
    @Getter @Setter
    private String instagram;
    
    @NotNull
    @Size(min=5)
    @Column(unique = true)
    @Getter @Setter
    private String email;
    
    @Getter @Setter
    private String city;
    
    @Getter @Setter
    private Locale placeBirth;
    
    @Getter @Setter
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dtBirth;
    
    @Getter @Setter
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dtStart;
    
    @Getter @Setter
    private String profilePic;
    
    @Getter @Setter
    private BloodType bloodType;
    
    @Getter @Setter
    private Boolean status;
    
    @ElementCollection
    @JoinTable(name = "official_position", joinColumns = @JoinColumn(name = "officialId"))
    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private List<PositionOfficial> positions;
    
    @CreatedBy
    @Getter @Setter
    @OneToOne
    private Account userCreator;
    
    @CreatedDate
    @Getter @Setter
    private Date dateCreate;
    
 
}