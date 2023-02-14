package net.novauniverse.bedwars.game.shop;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.Bedwars;
import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.enums.Trap;
import net.novauniverse.bedwars.game.enums.Upgrades;
import net.novauniverse.bedwars.game.holder.UpgradeShopHolder;
import net.novauniverse.bedwars.game.object.Price;
import net.novauniverse.bedwars.game.object.base.BaseData;
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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UpgradeShop {
	public void display(Player player) {
		UpgradeShopHolder holder = new UpgradeShopHolder();
		Inventory inventory = Bukkit.getServer().createInventory(holder, 54, BedwarsNPC.TEAM_UPGRADE_SHOP_NAME);
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

		inventory.setItem(10, Upgrades.SHARPNESS.asShopItem(team));
		inventory.setItem(11, Upgrades.PROTECTION.asShopItem(team));
		inventory.setItem(12, Upgrades.FORGE.asShopItem(team));
		inventory.setItem(19, Upgrades.HASTE.asShopItem(team));
		inventory.setItem(20, Upgrades.HEALPOOL.asShopItem(team));
		inventory.setItem(14, Trap.BLINDNESS.asShopItem(team));
		inventory.setItem(15, Trap.ALARM.asShopItem(team));
		inventory.setItem(16, Trap.COUNTER_OFFENSIVE.asShopItem(team));
		inventory.setItem(23, Trap.MINING_FATIGUE.asShopItem(team));

		ItemBuilder slot = new ItemBuilder(Material.BARRIER);

		inventory.setItem(39, new ItemBuilder(slot.build()).setName(ChatColor.RED + "Trap Slot #1").build());
		inventory.setItem(40, new ItemBuilder(slot.build()).setName(ChatColor.RED + "Trap Slot #2").build());
		inventory.setItem(41, new ItemBuilder(slot.build()).setName(ChatColor.RED + "Trap Slot #3").build());

		if (data.getTraps().size() >= 1) {
			inventory.setItem(39, data.getTraps().get(0).getStack());
		}

		if (data.getTraps().size() >= 2) {
			inventory.setItem(40, data.getTraps().get(1).getStack());
		}

		if (data.getTraps().size() >= 3) {
			inventory.setItem(41, data.getTraps().get(2).getStack());
		}

		BaseData finalData = data;
		holder.addClickCallback(inventoryClickEvent -> {
			if (inventoryClickEvent.getSlot() == 10) {
				if (!finalData.hasSharpness()) {
						if (Price.canBuy(player, Upgrades.SHARPNESS.getPrice())) {
							Price.buyUpgrade(player, Upgrades.SHARPNESS.getPrice());
							finalData.setSharpness(true);
							finalData.getOwner().getOnlinePlayers().forEach(p -> {
								VersionIndependentSound.NOTE_PLING.play(player);
								p.sendMessage(ChatColor.AQUA + player.getName() + " bought Sharpness");
							});
						} else {
							player.sendMessage(ChatColor.RED + "You dont have enough materials to buy this upgrade.");
						}
				}
			} else if (inventoryClickEvent.getSlot() == 11) {
				if (finalData.getProtectionLevel() < 4) {
					if (Price.canBuy(player, Upgrades.PROTECTION.getTieredUpgrades().get(finalData.getProtectionLevel()).getPrice())) {
						Price.buyUpgrade(player, Upgrades.PROTECTION.getTieredUpgrades().get(finalData.getProtectionLevel()).getPrice());
						finalData.setProtectionLevel(finalData.getProtectionLevel() + 1);
						finalData.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.NOTE_PLING.play(player);
							p.sendMessage(ChatColor.AQUA + player.getName() + " bought Protection " + finalData.getProtectionLevel());
						});
					} else {
						player.sendMessage(ChatColor.RED + "You dont have enough materials to buy this upgrade.");
					}
				}
			} else if (inventoryClickEvent.getSlot() == 12) {
				if (finalData.getForgeLevel() < 4) {
					if (Price.canBuy(player, Upgrades.FORGE.getTieredUpgrades().get(finalData.getForgeLevel()).getPrice())) {
						Price.buyUpgrade(player, Upgrades.FORGE.getTieredUpgrades().get(finalData.getForgeLevel()).getPrice());
						finalData.setForgeLevel(finalData.getForgeLevel() + 1);
						NovaBedwars.getInstance().getGame().buyForgeUpgrade(player, finalData.getForgeLevel());
						finalData.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.NOTE_PLING.play(player);
							p.sendMessage(ChatColor.AQUA + player.getName() + " bought Forge Upgrade " + finalData.getForgeLevel());
						});
					} else {
						player.sendMessage(ChatColor.RED + "You dont have enough materials to buy this upgrade.");
					}
				}
			} else if (inventoryClickEvent.getSlot() == 19) {
				if (finalData.getHasteLevel() < 2) {
					if (Price.canBuy(player, Upgrades.HASTE.getTieredUpgrades().get(finalData.getHasteLevel()).getPrice())) {
						Price.buyUpgrade(player, Upgrades.HASTE.getTieredUpgrades().get(finalData.getHasteLevel()).getPrice());
						finalData.setHasteLevel(finalData.getHasteLevel() + 1);
						finalData.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.NOTE_PLING.play(player);
							p.sendMessage(ChatColor.AQUA + player.getName() + " bought Haste " + finalData.getHasteLevel());
						});
					} else {
						player.sendMessage(ChatColor.RED + "You dont have enough materials to buy this upgrade.");
					}
				}
			} else if (inventoryClickEvent.getSlot() == 20) {
				if (!finalData.hasHealPool()) {
					if (Price.canBuy(player, Upgrades.HEALPOOL.getPrice())) {
						Price.buyUpgrade(player, Upgrades.HEALPOOL.getPrice());
						finalData.setHealPool(true);
						finalData.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.NOTE_PLING.play(player);
							p.sendMessage(ChatColor.AQUA + player.getName() + " bought Heal Pool");
						});
					} else {
						player.sendMessage(ChatColor.RED + "You dont have enough materials to buy this upgrade.");
					}
				}
			} else if (inventoryClickEvent.getSlot() == 14) {
				if (finalData.getTraps().size() < 3) {
					if (Price.canBuy(player, Trap.currentPrice(team))) {
						Price.buyUpgrade(player, Trap.currentPrice(team));
						finalData.addTrap(Trap.BLINDNESS);
						finalData.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.NOTE_PLING.play(player);
							p.sendMessage(ChatColor.AQUA + player.getName() + " bought Blindness Trap");
						});
					} else {
						player.sendMessage(ChatColor.RED + "You dont have enough materials to buy this upgrade.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You cant buy more than " + Bedwars.MAX_TRAP_AMOUNT + " Traps.");
				}
			} else if (inventoryClickEvent.getSlot() == 15) {
				if (finalData.getTraps().size() < 3) {
					if (Price.canBuy(player, Trap.currentPrice(team))) {
						Price.buyUpgrade(player, Trap.currentPrice(team));
						finalData.addTrap(Trap.ALARM);
						finalData.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.NOTE_PLING.play(player);
							p.sendMessage(ChatColor.AQUA + player.getName() + " bought Alarm Trap");
						});
					} else {
						player.sendMessage(ChatColor.RED + "You dont have enough materials to buy this upgrade.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You cant buy more than " + Bedwars.MAX_TRAP_AMOUNT + " Traps.");
				}
			} else if (inventoryClickEvent.getSlot() == 16) {
				if (finalData.getTraps().size() < 3) {
					if (Price.canBuy(player, Trap.currentPrice(team))) {
						Price.buyUpgrade(player, Trap.currentPrice(team));
						finalData.addTrap(Trap.COUNTER_OFFENSIVE);
						finalData.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.NOTE_PLING.play(player);
							p.sendMessage(ChatColor.AQUA + player.getName() + " bought Counter-Offensive Trap");
						});
					} else {
						player.sendMessage(ChatColor.RED + "You dont have enough materials to buy this upgrade.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You cant buy more than " + Bedwars.MAX_TRAP_AMOUNT + " Traps.");
				}
			} else if (inventoryClickEvent.getSlot() == 23) {
				if (finalData.getTraps().size() < 3) {
					if (Price.canBuy(player, Trap.currentPrice(team))) {
						Price.buyUpgrade(player, Trap.currentPrice(team));
						finalData.addTrap(Trap.MINING_FATIGUE);
						finalData.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.NOTE_PLING.play(player);
							p.sendMessage(ChatColor.AQUA + player.getName() + " bought Mining Fatigue Trap");
						});
					} else {
						player.sendMessage(ChatColor.RED + "You dont have enough materials to buy this upgrade.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You cant buy more than " + Bedwars.MAX_TRAP_AMOUNT + " Traps.");
				}
			}
			team.getOnlinePlayers().forEach(player1 -> NovaBedwars.getInstance().getGame().updatePlayerItems(player1));
			new UpgradeShop().display(player);
			NovaBedwars.getInstance().getGame().updatePlayerItems(player);
			return GUIAction.CANCEL_INTERACTION;
		});

		player.openInventory(inventory);
	}
}