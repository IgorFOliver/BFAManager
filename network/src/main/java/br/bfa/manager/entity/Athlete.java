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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.format.annotation.DateTimeFormat;

import br.bfa.manager.Enum.BloodType;
import br.bfa.manager.Enum.Experience;
import br.bfa.manager.Enum.Position;
import lombok.Getter;
import lombok.Setter;
 
@Entity
@EnableJpaAuditing
public class Athlete {
 
	public Athlete(String name) {
		this.name = name;
	}
    
    public Athlete(){}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Getter @Setter
	private Long id;
	
	@NotNull
	@Size(min=1, max=150)
    @Getter @Setter
    private String name;
    
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
    private String contractAdress;
    
    @Getter @Setter
    private Boolean contractSigned;
    
    @Getter @Setter
    private Date dtContractSigned;
    
    @Getter @Setter
    private Double cone;
    
    @Getter @Setter
    private Double shuttle;
    
    @Getter @Setter
    private Double dash;
    
    @Getter @Setter
    private Double verticalJump;
    
    @Getter @Setter
    private Double broadJump;
    
    @Getter @Setter
    private BloodType bloodType;
    
    @Getter @Setter
    private Boolean status;
    
    @Column(length=8)
    @Getter @Setter
    private Integer points;
    
    @Getter @Setter
    private Experience experience;
    
    @ElementCollection
    @JoinTable(name = "tblInterests", joinColumns = @JoinColumn(name = "personID"))
    @Column(name = "interest", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private List<Position> positions;
    
    @Getter @Setter
    @ManyToOne
    private Team team;
    
    @CreatedBy
    @Getter @Setter
    @OneToOne
    private Account userCreator;
    
    @CreatedDate
    @Getter @Setter
    private Date dateCreate;
    
}