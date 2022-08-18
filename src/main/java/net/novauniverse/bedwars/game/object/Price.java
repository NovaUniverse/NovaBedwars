package net.novauniverse.bedwars.game.object;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.enums.Items;
import net.novauniverse.bedwars.game.events.ItemBuyEvent;
import net.novauniverse.bedwars.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

public class Price {
	private final Material material;
	private final int price;

	public Price(Material material, int price) {
		this.material = material;
		this.price = price;
	}

	public Material getMaterial() {
		return material;
	}

	public int getValue() {
		return price;
	}

	public static boolean canBuy(Player player, Items item) {
		if (item.getPrice() == null) {
			return false;
		}
		int amountLeft = item.getPrice().getValue();
		ArrayList<Integer> slots = InventoryUtils.slotsWith(player.getInventory(), item.getPrice().getMaterial());
		for (Integer slot : slots) {
			amountLeft -= player.getInventory().getItem(slot).getAmount();
		}
		return amountLeft <= 0;
	}

	public static void buyItem(Items itemEnum, PlayerInventory inventory, ItemStack item, Player player) {
		boolean bought = true;
		if (InventoryUtils.slotsWith(inventory, null).size() == 0) {
			player.sendMessage(ChatColor.RED + "You dont have enough space in your inventory to buy that");
			bought = false;
		} else if (itemEnum.isArmor()) {
			if (NovaBedwars.getInstance().getGame().getAllPlayersArmor().get(player) == itemEnum.getArmorType()) {
				player.sendMessage(ChatColor.RED + "Fail: already have armor");
				bought = false;
			}
			if (NovaBedwars.getInstance().getGame().getAllPlayersArmor().get(player).getTier() < itemEnum.getArmorType().getTier()) {
				player.sendMessage(ChatColor.RED + "Fail: already have better tier");
				bought = false;
			}
			NovaBedwars.getInstance().getGame().getAllPlayersArmor().putIfAbsent(player, itemEnum.getArmorType());
			NovaBedwars.getInstance().getGame().getAllPlayersArmor().put(player, itemEnum.getArmorType());
			player.sendMessage(ChatColor.GREEN + "Success: armor bought");
		} else if (itemEnum.isTiered()) {

			if (itemEnum == Items.WOOD_PICKAXE) {

				for (int i = 0; i <= itemEnum.getTieredItems().size(); i++) {

					if (i == itemEnum.getTieredItems().size()) {
						player.sendMessage(ChatColor.RED + "Fail: tier limit reached");
						bought = false;
					}

					if (item.equals(itemEnum.getTieredItems().get(i))) {
						NovaBedwars.getInstance().getGame().getAllPlayersPickaxeTier().putIfAbsent(player, i + 1);
						NovaBedwars.getInstance().getGame().getAllPlayersPickaxeTier().putIfAbsent(player, i + 1);
						player.sendMessage(ChatColor.GREEN + "Success: new pickaxe tier bought");
					}

				}
			} else if (itemEnum == Items.WOOD_AXE) {

				for (int i = 0; i <= itemEnum.getTieredItems().size(); i++) {

					if (i == itemEnum.getTieredItems().size()) {
						player.sendMessage(ChatColor.RED + "Fail: tier limit reached");
						bought = false;
					}

					if (item.equals(itemEnum.getTieredItems().get(i))) {
						NovaBedwars.getInstance().getGame().getAllPlayersAxeTier().put(player, i + 1);
						NovaBedwars.getInstance().getGame().getAllPlayersAxeTier().putIfAbsent(player, i + 1);
						player.sendMessage(ChatColor.GREEN + "Success: new axe tier bought");
					}

				}
			}
		} else {
			inventory.addItem(itemEnum.getItemStack());
			//player.sendMessage(ChatColor.GREEN + "Success: normal item bought");
		}
		if (bought) {
			int amountLeft = itemEnum.getPrice().getValue();
			ArrayList<Integer> slots = InventoryUtils.slotsWith(player.getInventory(), itemEnum.getPrice().getMaterial());
			for (Integer slot : slots) {
				amountLeft -= player.getInventory().getItem(slot).getAmount();
				if (amountLeft <= 0) {
					if (amountLeft == 0) {
						player.getInventory().setItem(slot, new ItemStack(Material.AIR, 0));
					} else {
						player.getInventory().getItem(slot).setAmount(amountLeft * -1);
					}
					break;
				} else {
					player.getInventory().setItem(slot, new ItemStack(Material.AIR, 0));
				}
			}
			Bukkit.getPluginManager().callEvent(new ItemBuyEvent(itemEnum, player));
		}
	}
}