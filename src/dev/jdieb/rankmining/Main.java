package dev.jdieb.rankmining;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import dev.jdieb.rankmining.storage.MiningDataStorage;

public class Main extends JavaPlugin {

	private static Main plugin;
	private File file;
	private static FileConfiguration config;

	public static Main getMain() {
		return plugin;
	}

	public void createBauYaml(Main m) {
		file = new File(m.getDataFolder(), "baus.yml");
		config = YamlConfiguration.loadConfiguration(file);
		if (!file.exists()) {
			m.saveResource("baus.yml", false);
		}
	}

	public FileConfiguration getBaus() {
		return config;
	}

	@Override
	public void onEnable() {
		plugin = this;
		Metodos.csm("§a ");
		Metodos.csm("§a[RankMining] plugin ativado");
		Metodos.csm("§a ");
		MiningDataStorage.getDataStorage();
		String statusMysql = (MiningDataStorage.getStatus().equalsIgnoreCase("Nao conectado")) ? "§cNao esta conectado." : "§aEsta ativado.";
		Metodos.csm("§a[RankMining] MySql: " + statusMysql);
		Bukkit.getPluginCommand("blocos").setExecutor(new Comandos());
		Bukkit.getPluginManager().registerEvents(new Eventos(), this);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		this.createBauYaml(this);
		MiningDataStorage.closeConnection();
	}

	@Override
	public void onDisable() {
		Metodos.csm("§a ");
		Metodos.csm("§c[RankMining] plugin desativado");
		Metodos.csm("§a ");
	}

}
