package net.novauniverse.bedwars.game.object;

import net.novauniverse.bedwars.game.enums.Items;
import net.novauniverse.bedwars.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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
}