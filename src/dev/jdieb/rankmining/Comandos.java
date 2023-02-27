package dev.jdieb.rankmining;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.jdieb.rankmining.storage.MiningTempStorage;

public class Comandos implements CommandExecutor {

	private List<String> delay = new ArrayList<String>();

	@Override
	public boolean onCommand(CommandSender sd, Command cmd, String lb, String[] args) {
		if (!(sd instanceof Player)) {
			return true;
		}
		Player p = (Player) sd;
		if (delay.contains(p.getName())) {
			p.sendMessage(Metodos.getString("Comando.mensagem").replace("%segundos%", String.valueOf(Metodos.getInt("Comando.delay"))));
			return true;
		}
		delay.add(p.getName());
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getMain(), new Runnable() {
			@Override
			public void run() {
				delay.remove(p.getName());
			}
		}, Metodos.getInt("Comando.delay") * 20);
		if (MiningTempStorage.getRankMiningCache().containsKey(p)) {
			for (String ranks : Main.getMain().getConfig().getConfigurationSection("Rank.ranks").getKeys(false)) {
				int nextRank = (Integer.parseInt(ranks) + 1);
				int restBlock = 0;
				for (int i = MiningTempStorage.getRank(p); i < Metodos.getInt("Rank.ranks." + nextRank + ".valor"); i++) {
					restBlock++;
				}
				for (String msg : Main.getMain().getConfig().getStringList("Rank.config.mensagem-rank")) {
					if (MiningTempStorage.getRank(p) >= Metodos.getInt("Rank.ranks." + ranks + ".valor") && MiningTempStorage.getRank(p) < Metodos.getInt("Rank.ranks." + nextRank + ".valor")) {
						p.sendMessage(msg.replace("&", "§").replace("%rank%", ranks).replace("%blocks%", String.valueOf(MiningTempStorage.getRank(p))).replace("%blocosNextRank%", String.valueOf(Metodos.getInt("Rank.ranks." + nextRank + ".valor"))).replace("%RestBlock%", String.valueOf(restBlock)).replace("%nextRank%", String.valueOf(nextRank)));
					}
				}
			}
		} else {
			p.sendMessage("§cvocê não quebrou nada ainda.");
		}
		return false;
	}

}
