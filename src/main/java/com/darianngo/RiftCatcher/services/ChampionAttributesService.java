package com.darianngo.RiftCatcher.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.darianngo.RiftCatcher.entities.CaughtChampion;
import com.darianngo.RiftCatcher.entities.IVs;
import com.darianngo.RiftCatcher.entities.Nature;
import com.darianngo.RiftCatcher.entities.Stats;

@Service
public class ChampionAttributesService {

	public IVs generateIVs() {
		Random rand = new Random();
		IVs ivs = new IVs();

		ivs.setHpIV(rand.nextInt(32));
		ivs.setAttackIV(rand.nextInt(32));
		ivs.setDefenseIV(rand.nextInt(32));
		ivs.setSpAtkIV(rand.nextInt(32));
		ivs.setSpDefIV(rand.nextInt(32));
		ivs.setSpeedIV(rand.nextInt(32));

		return ivs;
	}

	public void calculateAndSetChampionStats(CaughtChampion caughtChampion) {
		Stats baseStats = caughtChampion.getChampion().getBaseStats();
		IVs championIVs = caughtChampion.getIvs();
		int level = caughtChampion.getLevel();
		Nature nature = caughtChampion.getNature();

		Map<String, Double> growthMultipliers = Map.of("hp", baseStats.getHpGrowth(), "attack",
				baseStats.getAttackGrowth(), "defense", baseStats.getDefenseGrowth(), "spAtk",
				baseStats.getSpAtkGrowth(), "spDef", baseStats.getSpDefGrowth(), "speed", baseStats.getSpeedGrowth());

		Map<String, Float> natureMultipliers = getNatureMultipliers(nature);

		// Calculate and set stats for all attributes
		caughtChampion.setCurrentHp(calculateStat(baseStats.getHp(), championIVs.getHpIV(), level,
				growthMultipliers.get("hp"), natureMultipliers.get("hp")));
		caughtChampion.setCurrentAttack(calculateStat(baseStats.getAttack(), championIVs.getAttackIV(), level,
				growthMultipliers.get("attack"), natureMultipliers.get("attack")));
		caughtChampion.setCurrentDefense(calculateStat(baseStats.getDefense(), championIVs.getDefenseIV(), level,
				growthMultipliers.get("defense"), natureMultipliers.get("defense")));
		caughtChampion.setCurrentSpAtk(calculateStat(baseStats.getSpAtk(), championIVs.getSpAtkIV(), level,
				growthMultipliers.get("spAtk"), natureMultipliers.get("spAtk")));
		caughtChampion.setCurrentSpDef(calculateStat(baseStats.getSpDef(), championIVs.getSpDefIV(), level,
				growthMultipliers.get("spDef"), natureMultipliers.get("spDef")));
		caughtChampion.setCurrentSpeed(calculateStat(baseStats.getSpeed(), championIVs.getSpeedIV(), level,
				growthMultipliers.get("speed"), natureMultipliers.get("speed")));
	}

	private int calculateStat(int baseStat, int iv, int level, double growthMultiplier, float natureMultiplier) {
		float rawStat = (float) ((baseStat + iv) * (1 + (level * growthMultiplier)) * natureMultiplier);
		int scaledStat = Math.round(rawStat / 100); // Scale down by a factor of 100
		return scaledStat;
	}

	// Method to fetch nature multipliers based on the champion's nature
	private Map<String, Float> getNatureMultipliers(Nature nature) {
		Map<String, Float> natureMultipliers = new HashMap<>();
		natureMultipliers.put("hp", 1.0f);
		natureMultipliers.put("attack", 1.0f);
		natureMultipliers.put("defense", 1.0f);
		natureMultipliers.put("spAtk", 1.0f);
		natureMultipliers.put("spDef", 1.0f);
		natureMultipliers.put("speed", 1.0f);

		if (nature != null) {
			natureMultipliers.put(nature.getBoostedStat(), 1.1f);
			natureMultipliers.put(nature.getDecreasedStat(), 0.9f);
		}

		return natureMultipliers;
	}
}
