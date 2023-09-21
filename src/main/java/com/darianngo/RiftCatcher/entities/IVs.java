package com.darianngo.RiftCatcher.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class IVs {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer hpIV;
	private Integer attackIV;
	private Integer defenseIV;
	private Integer spAtkIV;
	private Integer spDefIV;
	private Integer speedIV;
}
