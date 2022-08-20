package net.novauniverse.bedwars.game.shop;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.enums.Upgrades;
import net.novauniverse.bedwars.game.events.AttemptUpgradeBuyEvent;
import net.novauniverse.bedwars.game.holder.UpgradeShopHolder;
import net.novauniverse.bedwars.game.object.Price;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UpgradeShop {
	public void display(Player player) {
		UpgradeShopHolder holder = new UpgradeShopHolder();
		Inventory inventory = Bukkit.getServer().createInventory(holder, 9, BedwarsNPC.TEAM_UPGRADE_SHOP_NAME);
		ItemStack bg = new ItemBuilder(VersionIndependentUtils.get().getColoredItem(DyeColor.GRAY, ColoredBlockType.GLASS_PANE)).setName(" ").setAmount(1).build();
		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, bg);
		}
		BaseData data = null;
		Team team = TeamManager.getTeamManager().getPlayerTeam(player.getUniqueId());
		for (BaseData baseData : NovaBedwars.getInstance().getGame().getBases()) {
			if (baseData.getOwner().equals(team)) {
				data = baseData;
				break;
			}
		}

		inventory.setItem(0, Upgrades.SHARPNESS.asShopItem(team));
		inventory.setItem(4, Upgrades.PROTECTION.asShopItem(team));
		inventory.setItem(8, Upgrades.FORGE.asShopItem(team));

		BaseData finalData = data;
		holder.addClickCallback(inventoryClickEvent -> {
			boolean success = false;
			Upgrades upgrade = null;
			if (inventoryClickEvent.getSlot() == 0) {
				upgrade = Upgrades.SHARPNESS;
				if (Price.canBuy(player, Upgrades.SHARPNESS.getPrice())) {
					if (!finalData.hasSharpness()) {
						Price.buyUpgrade(player, Upgrades.SHARPNESS.getPrice());
						finalData.setSharpness(true);
						finalData.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.NOTE_PLING.play(player);
							p.sendMessage(ChatColor.AQUA + player.getName() + " bought sharpness");
						});
						success = true;
					}
				}
			} else if (inventoryClickEvent.getSlot() == 4) {
				upgrade = Upgrades.PROTECTION;
				if (finalData.getProtectionLevel() < 4) {
					if (Price.canBuy(player, Upgrades.PROTECTION.getTieredUpgrades().get(finalData.getProtectionLevel()).getPrice())) {
						Price.buyUpgrade(player, Upgrades.PROTECTION.getTieredUpgrades().get(finalData.getProtectionLevel()).getPrice());
						finalData.setProtectionLevel(finalData.getProtectionLevel() + 1);
						success = true;
						finalData.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.NOTE_PLING.play(player);
							p.sendMessage(ChatColor.AQUA + player.getName() + " bought protection " + finalData.getProtectionLevel());
						});
					}
				}
			} else if (inventoryClickEvent.getSlot() == 8) {
				upgrade = Upgrades.FORGE;
				if (finalData.getForgeLevel() < 4) {
					if (Price.canBuy(player, Upgrades.FORGE.getTieredUpgrades().get(finalData.getForgeLevel()).getPrice())) {
						Price.buyUpgrade(player, Upgrades.FORGE.getTieredUpgrades().get(finalData.getForgeLevel()).getPrice());
						finalData.setForgeLevel(finalData.getForgeLevel() + 1);
						success = true;
						NovaBedwars.getInstance().getGame().buyForgeUpgrade(player, finalData.getForgeLevel());
						finalData.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.NOTE_PLING.play(player);
							p.sendMessage(ChatColor.AQUA + player.getName() + " bought forge upgrade " + finalData.getForgeLevel());
						});
					}
				}
			}
			team.getOnlinePlayers().forEach(player1 -> NovaBedwars.getInstance().getGame().updatePlayerItems(player1));
			Bukkit.getPluginManager().callEvent(new AttemptUpgradeBuyEvent(upgrade, success, player, team));
			return GUIAction.CANCEL_INTERACTION;
		});

		player.openInventory(inventory);
	}
}