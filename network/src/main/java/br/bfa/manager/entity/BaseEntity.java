package br.bfa.manager.entity;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.joda.time.LocalDateTime;
import org.springframework.data.annotation.Version;

import lombok.Data;

@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static final String NOT_DELETED = "deleted_on > CURRENT_TIMESTAMP OR deleted_on IS NULL";

	@Version
	protected Long version;

	protected LocalDateTime createdOn;

	protected LocalDateTime updatedOn;

	protected LocalDateTime deletedOn;

	protected Long createdBy;

	protected Long updatedBy;

}