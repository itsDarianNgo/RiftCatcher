//package com.darianngo.RiftCatcher.utils;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.darianngo.RiftCatcher.entities.StarterChampion;
//import com.darianngo.RiftCatcher.repositories.StarterChampionRepository;
//
//@Configuration
//public class DataInitializer {
//
//	@Bean
//	public CommandLineRunner initData(StarterChampionRepository repository) {
//		return args -> {
//			// Check if the table is empty
//			if (repository.count() == 0) {
//				// Populate the database with starter champions data
//				List<StarterChampion> champions = Arrays
//						.asList(new StarterChampion(null, "Ashe", "Freljord", "Ashe's arrows can slow her enemies, providing her with better control over the battlefield.", "Frost's Focus", "Once a day, players can activate this bonus to significantly increase their chances of a successful catch on their next attempt. This reflects Ashe's ability to slow and focus on targets without making it an automatic win.")
//				// ... add other champions similarly
//				);
//				repository.saveAll(champions);
//			}
//		};
//	}
//}
