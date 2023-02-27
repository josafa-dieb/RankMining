package dev.jdieb.rankmining;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev.jdieb.rankmining.storage.MiningDataStorage;
import dev.jdieb.rankmining.storage.MiningTempStorage;

public class Metodos {

	public static void csm(String msg) {
		Bukkit.getConsoleSender().sendMessage(msg);
	}

	public static String getString(String local) {
		return Main.getMain().getConfig().getString(local).replace("&", "§");
	}

	public static boolean getBoolean(String local) {
		return Main.getMain().getConfig().getBoolean(local);
	}

	public static int getInt(String local) {
		return Main.getMain().getConfig().getInt(local);
	}

	public static double getDouble(String local) {
		return Main.getMain().getConfig().getDouble(local);
	}

	public static boolean hasPlayer(String p) {
		p = p.toLowerCase();
		try {
			PreparedStatement r = MiningDataStorage.getDataStorage().prepareStatement("SELECT player FROM dados WHERE player = '" + p + "';");
			ResultSet rs = r.executeQuery();
			while (rs.next()) {
				String query = rs.getString("player");
				if (query.equalsIgnoreCase(p)) {
					MiningDataStorage.closeConnection();
					return true;
				}
			}
		} catch (SQLException e) {
			MiningDataStorage.closeConnection();
			return false;
		}
		MiningDataStorage.closeConnection();
		return false;
	}

	public static void loadData(String p) {
		p = p.toLowerCase();
		try {
			PreparedStatement r = MiningDataStorage.getDataStorage().prepareStatement("SELECT pontos FROM dados WHERE player = '" + p + "'");
			ResultSet rs = r.executeQuery();
			while (rs.next()) {
				int pontos = rs.getInt("pontos");
				Player player = Bukkit.getPlayerExact(p);
				MiningTempStorage.setRank(player, pontos);
			}
		} catch (SQLException e) {

		}
	}

	public static void saveData(String p) {
		p = p.toLowerCase();
		int pts = MiningTempStorage.getRank(Bukkit.getPlayerExact(p));
		PreparedStatement pst;
		try {
			pst = MiningDataStorage.getDataStorage().prepareStatement("INSERT INTO dados VALUES('" + p + "', " + pts + ")");
			pst.executeUpdate();
			MiningDataStorage.closeConnection();
		} catch (SQLException e) {
			try {
				pst = MiningDataStorage.getDataStorage().prepareStatement("UPDATE dados SET player = '" + p + "', pontos = " + pts + " WHERE player = '" + p + "'");
				pst.executeUpdate();
				MiningDataStorage.closeConnection();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

	}
}
