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
//				List<StarterChampion> champions = Arrays.asList(
//						// Previously added champion
//						new StarterChampion(null, "Ashe", "Freljord",
//								"Ashe's arrows can slow her enemies, providing her with better control over the battlefield.",
//								"Frost's Focus",
//								"Once a day, players can activate this bonus to significantly increase their chances of a successful catch on their next attempt."),
//
//						// New champions based on the provided data
//						new StarterChampion(null, "Garen", "Demacia", "Demacians value honor and discipline.",
//								"Demacian Valor",
//								"Players starting with Garen have a slightly increased chance of catching champions on their first try every day, rewarding their discipline."),
//						new StarterChampion(null, "Darius", "Noxus", "Noxus values strength and conquest.",
//								"Conqueror's Might",
//								"After successfully catching three champions in a row, players get a temporary boost to catch rates, representing the momentum of conquest."),
//						new StarterChampion(null, "Caitlyn", "Piltover",
//								"Caitlyn is Piltover's best shot and is known for laying traps for her enemies.",
//								"Yordle Snap Trap",
//								"Once a day, players can lay a trap that increases their chances of encountering a rare or legendary champion in the next hour."),
//						new StarterChampion(null, "Jinx", "Zaun", "Jinx is all about chaos and unpredictability.",
//								"Chaotic Luck",
//								"Every day, players get a random boost on catching one specific champion type, reflecting the randomness of Jinx's nature."),
//						new StarterChampion(null, "Yasuo", "Ionia", "Yasuo is about balance and flow.", "Wind's Grace",
//								"After failing to catch a champion, players get a temporary boost in catch rates for the next one."),
//						new StarterChampion(null, "Yorick", "Shadow Isles",
//								"Yorick has the unique ability to summon Mist Walkers and raise the dead.",
//								"Mist's Aid",
//								"Once per day, when a player fails to catch a champion, there's a chance a Mist Walker will retrieve it for them."),
//						new StarterChampion(null, "Azir", "Shurima", "Azir is about empire building and resurrection.",
//								"Emperor's Favor",
//								"Once a day, players can retry catching a champion they just missed."),
//						new StarterChampion(null, "Leona", "Targon",
//								"Leona is the embodiment of the sun's radiant energy.", "Radiant Dawn",
//								"During specific daylight hours, players have a slightly increased catch rate, harnessing the sun's power."),
//						new StarterChampion(null, "Miss Fortune", "Bilgewater",
//								"Miss Fortune is a bounty hunter who's always on the hunt for treasures.",
//								"Bounty Seeker",
//								"Players get extra gold when they catch a rare or legendary champion."),
//
//
//						new StarterChampion(null, "Tristana", "Bandle City",
//								"Tristana is known for her daring leaps into battle and her explosive charges.",
//								"Rocket Jump",
//								"Once per day, players can get a second chance to catch a champion they just failed to catch."));
//
//				// Save all champions to the database
//				repository.saveAll(champions);
//			}
//		};
//	}
//}
