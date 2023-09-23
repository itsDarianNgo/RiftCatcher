package com.darianngo.RiftCatcher.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ChampionDataPopulationService {

	private String jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";
	private String username = "postgres";
	private String password = "bondstone";

	public List<String[]> readCsv(String filePath) throws Exception {
		List<String[]> data = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				data.add(line.split(","));
			}
		}
		return data;
	}

	public void insertDataToDatabase(List<String[]> data) throws Exception {
		try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {

			// SQL Queries for Role and Rarity insertion
			String sqlRole = "INSERT INTO role (name) VALUES (?) RETURNING id;";
			String sqlRarity = "INSERT INTO champion_rarity (rarity) VALUES (?) RETURNING id;";

			// SQL Query for Champion insertion
			String sqlChampion = "INSERT INTO champion (name, role_id, rarity_id, base_stats_id) VALUES (?, ?, ?, ?) RETURNING id;";

			// SQL for stats insertion
			String sqlStats = "INSERT INTO stats (hp, hp_growth, attack, attack_growth, defense, defense_growth, sp_atk, sp_atk_growth, sp_def, sp_def_growth, speed, speed_growth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;";

			try (PreparedStatement psRole = connection.prepareStatement(sqlRole);
					PreparedStatement psRarity = connection.prepareStatement(sqlRarity);
					PreparedStatement psChampion = connection.prepareStatement(sqlChampion);
					PreparedStatement psStats = connection.prepareStatement(sqlStats)) {

				for (String[] row : data) {

					// Check if Role exists
					Long roleId = null;
					String checkRole = "SELECT id FROM role WHERE name = ?;";
					try (PreparedStatement psCheckRole = connection.prepareStatement(checkRole)) {
						psCheckRole.setString(1, row[1]);
						ResultSet rsCheckRole = psCheckRole.executeQuery();
						if (rsCheckRole.next()) {
							roleId = rsCheckRole.getLong(1);
						}
					}

					// Insert Role if it doesn't exist
					if (roleId == null) {
						psRole.setString(1, row[1]);
						ResultSet rsRole = psRole.executeQuery();
						if (rsRole.next()) {
							roleId = rsRole.getLong(1);
						}
					}

					// Check if Rarity exists
					Long rarityId = null;
					String checkRarity = "SELECT id FROM champion_rarity WHERE rarity = ?;";
					try (PreparedStatement psCheckRarity = connection.prepareStatement(checkRarity)) {
						psCheckRarity.setString(1, row[2]);
						ResultSet rsCheckRarity = psCheckRarity.executeQuery();
						if (rsCheckRarity.next()) {
							rarityId = rsCheckRarity.getLong(1);
						}
					}

					// Insert Rarity if it doesn't exist
					if (rarityId == null) {
						psRarity.setString(1, row[2]);
						ResultSet rsRarity = psRarity.executeQuery();
						if (rsRarity.next()) {
							rarityId = rsRarity.getLong(1);
						}
					}

					// Insert Stats
					for (int i = 3; i < 15; i++) {
						if (i == 3 || i == 5 || i == 7 || i == 9 || i == 11 || i == 13) {
							psStats.setInt(i - 2, Integer.parseInt(row[i]));
						} else {
							psStats.setDouble(i - 2, Double.parseDouble(row[i]));
						}
					}
					ResultSet rsStats = psStats.executeQuery();
					Long statsId = rsStats.next() ? rsStats.getLong(1) : null;

					// Insert into Champion
					psChampion.setString(1, row[0]);
					psChampion.setLong(2, roleId);
					psChampion.setLong(3, rarityId);
					psChampion.setLong(4, statsId);
					ResultSet rsChampion = psChampion.executeQuery();

				}
			}
		}
	}

	public void populateDatabaseFromCsv(String csvPath) throws Exception {
		List<String[]> data = readCsv(csvPath);
		data.remove(0); // Remove the header
		insertDataToDatabase(data);
	}
	
}
