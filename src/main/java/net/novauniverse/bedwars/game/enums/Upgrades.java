package net.novauniverse.bedwars.game.enums;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.object.Price;
import net.novauniverse.bedwars.game.object.TieredUpgrade;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum Upgrades {
	PROTECTION(new ItemBuilder(Material.IRON_CHESTPLATE).setAmount(1).setName(ChatColor.BLUE + "" + ChatColor.BOLD + "Protection").build(), new TieredUpgrade(new Price(Material.DIAMOND, 2)), new TieredUpgrade(new Price(Material.DIAMOND, 4)), new TieredUpgrade(new Price(Material.DIAMOND, 8)), new TieredUpgrade(new Price(Material.DIAMOND, 16))),
	HASTE(new ItemBuilder(VersionIndependentMaterial.GOLDEN_PICKAXE).setAmount(1).setName(ChatColor.GOLD + "" + ChatColor.BOLD + "Haste").build(), new TieredUpgrade(new Price(Material.DIAMOND, 2)), new TieredUpgrade(new Price(Material.DIAMOND, 4))),
	FORGE(new ItemBuilder(Material.FURNACE).setAmount(1).setName(ChatColor.GRAY + "" + ChatColor.BOLD +  "Forge Upgrade").build(), new TieredUpgrade(new Price(Material.DIAMOND, 2)), new TieredUpgrade(new Price(Material.DIAMOND, 4)), new TieredUpgrade(new Price(Material.DIAMOND, 6)), new TieredUpgrade(new Price(Material.DIAMOND, 8))),
	SHARPNESS(new ItemBuilder(Material.IRON_SWORD).setAmount(1).setName(ChatColor.RED + "" + ChatColor.BOLD + "Sharpness").build(), new Price(Material.DIAMOND, 4)),
	HEALPOOL(new ItemBuilder(Material.BEACON).setAmount(1).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Heal Pool").build(), new Price(Material.DIAMOND, 1));

	private ItemStack displayItem;
	private List<TieredUpgrade> tieredUpgrade;
	private Price price;

	private boolean tiered;

	Upgrades(ItemStack displayItem, TieredUpgrade... upgrades) {
		this.displayItem = displayItem;
		this.tieredUpgrade = Arrays.asList(upgrades);
		tiered = true;
	}

	Upgrades() {

	}

	Upgrades(ItemStack displayItem, Price price) {
		this.displayItem = displayItem;
		this.price = price;
		tiered = false;
	}

	public ItemStack asShopItem(Team team) {
		ItemStack item = displayItem.clone();
		ItemMeta meta = item.getItemMeta();
		meta.setLore(addLore(team));
		item.setItemMeta(meta);
		return item;
	}

	public boolean isTiered() {
		return tiered;
	}

	public Price getPrice() {
		return price;
	}

	public List<TieredUpgrade> getTieredUpgrades() {
		return tieredUpgrade;
	}

	public List<String> addLore(Team team) {
		BaseData data = NovaBedwars.getInstance().getGame().getBases().stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
		List<String> lore = new ArrayList<>();
		if (tieredUpgrade != null) {
			for (int i = 0; i < tieredUpgrade.size(); i++) {
				StringBuilder stringified = new StringBuilder(tieredUpgrade.get(i).getPrice().getMaterial().name().toLowerCase(Locale.ROOT).replace('_', ' '));
				if (tieredUpgrade.get(i).getPrice().getValue() >= 2) {
					stringified.append("s");
				}
				if (i < data.getDataFromUpgrade(this)) {
					lore.add(ChatColor.GREEN + "Tier " + (i + 1) + ": " + ChatColor.BOLD + tieredUpgrade.get(i).getPrice().getValue() + " " + stringified);
				} else if (i == data.getDataFromUpgrade(this)) {
					lore.add(ChatColor.WHITE + "> " + ChatColor.GRAY + "Tier " + (i + 1) + ": " + ChatColor.WHITE + tieredUpgrade.get(i).getPrice().getValue() + " " + stringified);
				} else {
					lore.add(ChatColor.GRAY + "Tier " + (i + 1) + ": " + ChatColor.DARK_GRAY + tieredUpgrade.get(i).getPrice().getValue() + " " + stringified);
				}
			}
			if (data.getDataFromUpgrade(this) == tieredUpgrade.size()) {
				lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "MAXED!");
			}
		} else {
			String stringified = price.getMaterial().name().toLowerCase(Locale.ROOT);
			if (price.getValue() >= 2) {
				stringified += "s";
			}
			if (data.getDataFromUpgrade(this) == 1) {
				lore.add(ChatColor.GREEN + "Price: " + ChatColor.BOLD + price.getValue() + " " + stringified);
				lore.add(ChatColor.RED + "" + ChatColor.BOLD +  "You can only buy this once.");
			} else {
				lore.add(ChatColor.WHITE + "> " + ChatColor.GRAY + "Price: " + ChatColor.WHITE + price.getValue() + " " + stringified);
			}

		}
		return lore;
	}

}