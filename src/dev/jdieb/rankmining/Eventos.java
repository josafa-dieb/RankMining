package dev.jdieb.rankmining;

import java.util.ArrayList;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.jdieb.rankmining.storage.MiningTempStorage;

public class Eventos implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!MiningTempStorage.getRankMiningCache().containsKey(p)) {
			if (Metodos.hasPlayer(p.getName())) {
				Metodos.loadData(p.getName());
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (MiningTempStorage.getRankMiningCache().containsKey(p)) {
			Metodos.saveData(p.getName());
			MiningTempStorage.getRankMiningCache().remove(p);
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		Player p = e.getPlayer();
		if (MiningTempStorage.getRankMiningCache().containsKey(p)) {
			Metodos.saveData(p.getName());
			MiningTempStorage.getRankMiningCache().remove(p);
		}
	}

	@EventHandler
	public void onBreakEvent(BlockBreakEvent e) {
		if (!e.isCancelled()) {
			Player p = e.getPlayer();
			Block b = e.getBlock();
			String worldPlayer = p.getWorld().getName();
			for (String worldLiberado : Main.getMain().getConfig().getStringList("Mundos-Liberado")) {
				if (worldLiberado.equalsIgnoreCase(worldPlayer)) {
					if (Metodos.getBoolean("Blocos-Especificos")) {
						for (String BlocoEspecifico : Main.getMain().getConfig().getStringList("Blocos")) {
							String iad = BlocoEspecifico.split(",")[0].trim();
							// Item And Data
							int id = Integer.parseInt(iad.split(":")[0].trim());
							int data = Integer.parseInt(iad.split(":")[1].trim());
							ItemStack blockEspecific = new ItemStack(Material.getMaterial(id), 1, (short) data);
							ItemStack blockBreak = new ItemStack(b.getType(), 1, b.getData());
							if (blockEspecific.isSimilar(blockBreak)) {
								int lastRank = Metodos.getInt("Rank.config.ultimo");
								int pts = Integer.parseInt(BlocoEspecifico.split(",")[1].trim());
								MiningTempStorage.addRank(p, pts);
								if (MiningTempStorage.getRank(p) >= Metodos.getInt("Rank.ranks." + lastRank + ".valor")) {
									MiningTempStorage.setRank(p, 0);
								}
								for (String ranks : Main.getMain().getConfig().getConfigurationSection("Rank.ranks").getKeys(false)) {
									if (MiningTempStorage.getRank(p) == Metodos.getInt("Rank.ranks." + ranks + ".valor")) {
										b.getLocation().getBlock().setType(Material.CHEST);
										Chest bau = (Chest) b.getState();
										Inventory bauInv = bau.getBlockInventory();
										try {
											for (String itensBau : Main.getMain().getBaus().getConfigurationSection("Baus." + ranks + ".itens").getKeys(false)) {
												// dados do item
												int id_itensBau = Main.getMain().getBaus().getInt("Baus." + ranks + ".itens." + itensBau + ".id");
												int data_itensBau = Main.getMain().getBaus().getInt("Baus." + ranks + ".itens." + itensBau + ".data");
												int qnt_itensBau = Main.getMain().getBaus().getInt("Baus." + ranks + ".itens." + itensBau + ".quantidade");
												String nome_itensBau = Main.getMain().getBaus().getString("Baus." + ranks + ".itens." + itensBau + ".nome").replace("&", "§").replaceAll("%rank%", ranks);
												List<String> lore = new ArrayList<>();
												for (String txtLore : Main.getMain().getBaus().getStringList("Baus." + ranks + ".itens." + itensBau + ".lore")) {
													lore.add(txtLore.replace("&", "§").replaceAll("%rank%", ranks));
												}
												ItemStack item = new ItemStack(Material.getMaterial(id_itensBau), qnt_itensBau, (short) data_itensBau);
												if (!Main.getMain().getBaus().getBoolean("Baus." + ranks + ".itens." + itensBau + ".porcao")) {
													ItemMeta itemMeta = item.getItemMeta();
													itemMeta.setDisplayName(nome_itensBau);
													itemMeta.setLore(lore);
													for (String enchant : Main.getMain().getBaus().getStringList("Baus." + ranks + ".itens." + itensBau + ".encantos")) {
														String enchant_name = enchant.split("-")[0];
														Integer enchant_level = Integer.parseInt(enchant.split("-")[1]);
														itemMeta.addEnchant(Enchantment.getByName(enchant_name), enchant_level, true);
													}
													item.setItemMeta(itemMeta);

												} else {
													for (String effects : Main.getMain().getBaus().getStringList("Baus." + ranks + ".itens." + itensBau + ".efeitos")) {
														PotionMeta itemMeta = (PotionMeta) item.getItemMeta();
														itemMeta.setDisplayName(nome_itensBau);
														itemMeta.setLore(lore);
														String efeitos = effects.split("-")[0];
														Integer time = Integer.parseInt(effects.split("-")[1]);
														Integer level = Integer.parseInt(effects.split("-")[2]);
														Boolean effect_new = Boolean.parseBoolean(effects.split("-")[3]);
														itemMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(efeitos), time, level), effect_new);
														item.setItemMeta(itemMeta);
													}
												}
												bauInv.addItem(item);
											}
										} catch (NullPointerException e2) {
											final String ERRO = "[AVISO] para cada nivel, voce precisa criar um bau!";
											new Throwable(ERRO);
										}
										if (Metodos.getBoolean("Remover-bau")) {
											Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getMain(), new Runnable() {
												@Override
												public void run() {
													if (Metodos.getBoolean("Efeitos.particulas")) {
														bau.getLocation().getWorld().playEffect(bau.getLocation(), Effect.EXPLOSION_LARGE, 3018);
													}
													if (Metodos.getBoolean("Efeitos.som")) {
														bau.getLocation().getWorld().playSound(bau.getLocation(), Sound.EXPLODE, 2.95F, 1.5F);
													}
													if (!Metodos.getBoolean("Dropar-itens-do-bau")) {
														bauInv.clear();
													}
													bau.getBlock().setType(Material.AIR);
												}
											}, /*Metodos.getInt("Segundos-remover-bau")*/5 * 20);
										}
										p.sendMessage(Metodos.getString("Subiu-rank").replace("%rank%", ranks).replace("%blocos%", String.valueOf(Metodos.getInt("Rank.ranks." + ranks + ".valor"))));
										e.setCancelled(true);
									}
								}
							}

						}
					}
				}
			}
		}

	}

}
