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

	public int getPrice() {
		return price;
	}

	public static boolean canBuy(Player player, Items item) {
		int amountLeft = item.getPrice().getPrice();
		ArrayList<Integer> slots = InventoryUtils.slotsWith(player.getInventory(), item.getPrice().getMaterial());
		for (Integer slot : slots) {
			amountLeft -= player.getInventory().getItem(slot).getAmount();
		}
		return amountLeft <= 0;
	}

	public static void buyItem(Items itemEnum, PlayerInventory inventory, ItemStack item, Player player) {
		boolean bought = true;
		if (InventoryUtils.slotsWith(inventory, null).size() == 0) {
			Bukkit.broadcastMessage(ChatColor.RED + "Fail: no slots available");
			bought = false;
		}
		if (itemEnum.isArmor()) {
			if (NovaBedwars.getInstance().getAllPlayersArmor().get(player) == itemEnum.getArmorType()) {
				Bukkit.broadcastMessage(ChatColor.RED + "Fail: already have armor");
				bought = false;
			}
			NovaBedwars.getInstance().getAllPlayersArmor().putIfAbsent(player, itemEnum.getArmorType());
			NovaBedwars.getInstance().getAllPlayersArmor().put(player, itemEnum.getArmorType());
			Bukkit.broadcastMessage(ChatColor.GREEN + "Success: armor bought");
		} else if (itemEnum.isTiered()) {

			if (itemEnum == Items.WOOD_PICKAXE) {

				for (int i = 0; i <= itemEnum.getTieredItems().size(); i++)  {

					if (i == itemEnum.getTieredItems().size()) {
						Bukkit.broadcastMessage(ChatColor.RED + "Fail: tier limit reached");
						bought = false;
					}

					if (item.equals(itemEnum.getTieredItems().get(i))) {
						NovaBedwars.getInstance().getAllPlayersPickaxeTier().putIfAbsent(player, i + 1);
						NovaBedwars.getInstance().getAllPlayersPickaxeTier().putIfAbsent(player, i + 1);
						Bukkit.broadcastMessage(ChatColor.GREEN + "Success: new pickaxe tier bought");
					}

				}
			} else if (itemEnum == Items.WOOD_AXE) {

				for (int i = 0; i <= itemEnum.getTieredItems().size(); i++)  {

					if (i == itemEnum.getTieredItems().size()) {
						Bukkit.broadcastMessage(ChatColor.RED + "Fail: tier limit reached");
						bought = false;
					}

					if (item.equals(itemEnum.getTieredItems().get(i))) {
						NovaBedwars.getInstance().getAllPlayersAxeTier().put(player, i + 1);
						NovaBedwars.getInstance().getAllPlayersAxeTier().putIfAbsent(player, i + 1);
						Bukkit.broadcastMessage(ChatColor.GREEN + "Success: new axe tier bought");
					}

				}
			}
		} else {
			inventory.addItem(itemEnum.getItemStack());
			Bukkit.broadcastMessage(ChatColor.GREEN + "Success: normal item bought");
		}
		if (bought) {
			Bukkit.getPluginManager().callEvent(new ItemBuyEvent(itemEnum, player));
		}
	}
}