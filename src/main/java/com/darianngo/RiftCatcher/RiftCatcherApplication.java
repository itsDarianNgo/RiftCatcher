package com.darianngo.RiftCatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class RiftCatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiftCatcherApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner run(ChampionDataPopulationService dataService,
//			CloudinaryBatchUploadService uploadService) {
//		return args -> {
//			dataService.populateDatabaseFromCsv("C:/Users/daria/OneDrive/Desktop/RiftCatchers - Data/RiftCatcher - Champion Stats.csv");
//
//			// Parse the skin rarities from the CSV
//			Map<String, String> championSkinRarities = uploadService
//					.parseSkinRaritiesFromCsv("C:/Users/daria/OneDrive/Desktop/RiftCatchers - Data/skins_data_original - Copy.csv");
//
//			// Execute the batch upload for Cloudinary
//			uploadService.uploadChampionImages(
//					Paths.get("C:/Users/daria/OneDrive/Desktop/RiftCatchers - Data/champion_skins"),
//					championSkinRarities);
//		};
//	}
}