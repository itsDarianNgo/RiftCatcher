	package com.darianngo.RiftCatcher.entities;
	
	import jakarta.persistence.Entity;
	import jakarta.persistence.FetchType;
	import jakarta.persistence.GeneratedValue;
	import jakarta.persistence.GenerationType;
	import jakarta.persistence.Id;
	import jakarta.persistence.JoinColumn;
	import jakarta.persistence.ManyToOne;
	import lombok.Data;
	
	@Entity
	@Data
	public class ChampionSkin {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;
	
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "champion_id")
		private Champion champion;
	
		private String name;
		private String description;
		private String imageUrl;
	
		@ManyToOne
		private ChampionSkinRarity skinRarity; // Common, Rare, Epic, Legendary, Mythic, Ultimate
	
	}
