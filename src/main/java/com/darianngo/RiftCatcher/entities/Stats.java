package com.darianngo.RiftCatcher.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Stats {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer hp;
	private Double hpGrowth;

	private Integer attack;
	private Double attackGrowth;

	private Integer defense;
	private Double defenseGrowth;

	private Integer spAtk;
	private Double spAtkGrowth;

	private Integer spDef;
	private Double spDefGrowth;

	private Integer speed;
	private Double speedGrowth;
}
