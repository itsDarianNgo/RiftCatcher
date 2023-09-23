package com.darianngo.RiftCatcher.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.Champion;
import com.darianngo.RiftCatcher.entities.ChampionSkin;
import com.darianngo.RiftCatcher.entities.ChampionSkinRarity;
import com.darianngo.RiftCatcher.repositories.ChampionRepository;
import com.darianngo.RiftCatcher.repositories.ChampionSkinRarityRepository;
import com.darianngo.RiftCatcher.repositories.ChampionSkinRepository;

@Service
public class CloudinaryBatchUploadService {

	@Autowired
	private CloudinaryService cloudinaryService;

	@Autowired
	private ChampionSkinRepository championSkinRepository;

	@Autowired
	private ChampionRepository championRepository;

	@Autowired
	private ChampionSkinRarityRepository championSkinRarityRepository;

	private static final String UPLOADED_IMAGES_LOG = "uploaded_images.log";

	// Load the list of already uploaded images from the log file
	private Set<String> loadUploadedImages() {
		try {
			return new HashSet<>(Files.readAllLines(Paths.get(UPLOADED_IMAGES_LOG)));
		} catch (IOException e) {
			return new HashSet<>();
		}
	}

	// Log the uploaded image to the log file
	private void logUploadedImage(Path imagePath) {
		try {
			Files.write(Paths.get(UPLOADED_IMAGES_LOG), (imagePath.toString() + "\n").getBytes(),
					StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		} catch (IOException e) {
			System.err.println("Error logging uploaded image: " + imagePath);
			e.printStackTrace();
		}
	}

	public void uploadChampionImages(Path directoryPath, Map<String, String> championSkinRarities)
			throws IOException, InterruptedException {
		Set<String> uploadedImages = loadUploadedImages();

		Files.walk(directoryPath, 2).filter(Files::isRegularFile).forEach(file -> {
			try {
				if (!uploadedImages.contains(file.toString())) {
					processFile(file, championSkinRarities);
					logUploadedImage(file);
					Thread.sleep(100);
				}
			} catch (IOException | InterruptedException e) {
				System.err.println("Error processing file: " + file.toString());
				e.printStackTrace();
			}
		});
	}

	private void processFile(Path file, Map<String, String> championSkinRarities) throws IOException {
		Path championName = file.getParent().getFileName();
		String skinName = file.getFileName().toString().replace(".jpg", "");
		String folderPath = "RiftCatchers/" + championName.toString();
		String[] tags = { championName.toString(), skinName };

		if (cloudinaryService.imageWithTagExists(tags)) {
			return;
		}

		String imageURL = cloudinaryService.uploadFile(Files.readAllBytes(file), folderPath, tags);
		System.out.println(
				"Uploaded image for " + championName + "_" + skinName + " to Cloudinary with URL: " + imageURL); // Logging

		ChampionSkin skin = championSkinRepository.findByChampion_NameAndName(championName.toString(), skinName);
		if (skin == null) {
			// If the skin doesn't exist, create a new one
			skin = new ChampionSkin();
			skin.setName(skinName);

			// Retrieve the Champion entity by name
			Champion champion = championRepository.findByNameIgnoreCase(championName.toString());
			if (champion != null) {
				skin.setChampion(champion);
			} else {
				// Handle scenario when champion is not found
				System.err.println("Champion entity not found for: " + championName);
				return;
			}
		}

		// Fetch or create the ChampionSkinRarity based on the CSV data
		ChampionSkinRarity skinRarity = championSkinRarityRepository
				.findByRarity(championSkinRarities.get(championName.toString() + "_" + skinName));
		if (skinRarity == null) {
			skinRarity = new ChampionSkinRarity();
			skinRarity.setRarity(championSkinRarities.get(championName.toString() + "_" + skinName));
			championSkinRarityRepository.save(skinRarity);
		}

		skin.setSkinRarity(skinRarity);
		skin.setImageUrl(imageURL);
		championSkinRepository.save(skin);

		System.out.println((skin.getId() == null ? "Created" : "Updated") + " ChampionSkin entity for " + championName
				+ "_" + skinName + " with imageURL: " + imageURL); // Logging
	}

	public Map<String, String> parseSkinRaritiesFromCsv(String csvPath) throws IOException {
		Map<String, String> championSkinRarities = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
			String line;
			br.readLine(); // skip the header
			while ((line = br.readLine()) != null) {
				String[] columns = line.split(",");
				String key = columns[0] + "_" + columns[1]; // Champion_Skin as key
				String value = columns[3]; // Rarity as value
				championSkinRarities.put(key, value);
			}
		}
		return championSkinRarities;
	}

}
