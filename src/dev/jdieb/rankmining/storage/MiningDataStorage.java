package dev.jdieb.rankmining.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dev.jdieb.rankmining.Main;

public class MiningDataStorage {

	private static Connection con;
	private static String sqlCode;
	private static PreparedStatement prepare;
	private static String status = "Nao conectado";

	public static synchronized Connection getDataStorage() {
		String driverName = "org.sqlite.JDBC";
		try {
			Class.forName(driverName);
			con = DriverManager.getConnection("jdbc:sqlite:" + Main.getMain().getDataFolder() + "/storage.db");
			status = "Conectado com sucesso!";
			sqlCode = "CREATE TABLE IF NOT EXISTS dados (player VARCHAR(20) UNIQUE, pontos INT, primary key(player));";
			prepare = con.prepareStatement(sqlCode);
			prepare.execute();
			return con;
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			status = "Nao conectado";
			return null;
		}
	}

	public static String getStatus() {
		return status;
	}

	public static void closeConnection() {
		try {
			if (!MiningDataStorage.getDataStorage().isClosed()) {
				MiningDataStorage.getDataStorage().close();
				con = null;
				status = "Nao conectado";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
