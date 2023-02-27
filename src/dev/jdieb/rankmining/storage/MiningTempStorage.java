package dev.jdieb.rankmining.storage;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class MiningTempStorage {

	private static Map<Player, Integer> rankMining = new HashMap<>();

	public static Map<Player, Integer> getRankMiningCache() {
		return rankMining;
	}

	public static Integer setRank(Player p, int pts) {
		return rankMining.put(p, pts);
	}

	public static Integer addRank(Player p, int pts) {
		if (getRankMiningCache().containsKey(p)) {
			int oldValue = getRankMiningCache().get(p).intValue();
			int newValue = (oldValue + pts);
			return rankMining.put(p, newValue);
		} else {
			rankMining.put(p, pts);
		}
		return null;
	}

	public static Integer getRank(Player p) {
		return MiningTempStorage.getRankMiningCache().get(p).intValue();
	}

}
