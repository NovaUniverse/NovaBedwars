package net.novauniverse.bedwars.game.object;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.novauniverse.bedwars.game.enums.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TieredItem {
	private final ItemStack itemStack;
	private final Price price;
	private ItemStack shopItem;
	private ShopItem item;

	public TieredItem(ItemStack itemStack, Price price) {
		this.itemStack = itemStack;
		this.price = price;
	}

	public void setItem(ShopItem item) {
		this.item = item;
		shopItem = toShopItem();
	}

	public ShopItem getItem() {
		return item;
	}

	public ItemStack getShopItem() {
		return shopItem;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public Price getPrice() {
		return price;
	}

	private ItemStack toShopItem() {
		ItemStack item = getItemStack().clone();
		ItemMeta meta = item.getItemMeta();
		if (getPrice() != null) {
			meta.setLore(addLore(getPrice()));
		}
		meta.spigot().setUnbreakable(true);
		item.setItemMeta(meta);
		item = NBTEditor.set(item, 1, "bedwars", "isshopitem");
		item = NBTEditor.set(item, 1, "bedwars", "istiereditem");
		item = NBTEditor.set(item, getItem().name(), "bedwars", "tiereditemname");
		return item;
	}

	private List<String> addLore(Price price) {
		ChatColor color = null;
		if (price.getMaterial() == Material.EMERALD) {
			color = ChatColor.GREEN;
		} else if (price.getMaterial() == Material.IRON_INGOT) {
			color = ChatColor.GRAY;
		} else if (price.getMaterial() == Material.GOLD_INGOT) {
			color = ChatColor.GOLD;
		}
		String stringified = price.getMaterial().name().toLowerCase(Locale.ROOT).replace('_', ' ');
		if (price.getValue() >= 2) {
			stringified += "s";
		}
		List<String> lore = new ArrayList<>();
		lore.add(color + "" + ChatColor.BOLD + "" + price.getValue() + " " + stringified);
		return lore;
	}
}