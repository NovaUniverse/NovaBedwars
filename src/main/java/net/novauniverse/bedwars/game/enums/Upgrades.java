package net.novauniverse.bedwars.game.enums;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.object.Price;
import net.novauniverse.bedwars.game.object.TieredUpgrade;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public enum Upgrades {
	PROTECTION(new ItemBuilder(Material.IRON_CHESTPLATE).setAmount(1).setName(ChatColor.GRAY +"Team protection").build(),
			new TieredUpgrade(new Price(Material.DIAMOND, 2)),
			new TieredUpgrade(new Price(Material.DIAMOND, 4)),
			new TieredUpgrade(new Price(Material.DIAMOND, 8)),
			new TieredUpgrade(new Price(Material.DIAMOND, 16))), HASTE,
	FORGE(new ItemBuilder(Material.FURNACE).setAmount(1).setName(ChatColor.GRAY + "Team forge").build(),
			new TieredUpgrade(new Price(Material.DIAMOND, 2)),
			new TieredUpgrade(new Price(Material.DIAMOND, 4)),
			new TieredUpgrade(new Price(Material.DIAMOND, 6)),
			new TieredUpgrade(new Price(Material.DIAMOND, 8))), SHARPNESS(new ItemBuilder(Material.IRON_SWORD)
			.setAmount(1).setName(ChatColor.GRAY + "Team sharpness").build(), new Price(Material.DIAMOND, 4)), HEALPOOL, TRAP_BLIND, TRAP_FATIGUE, TRAP_COUNTER, TRAP_ALARM;

	private ItemStack displayItem;
	private List<TieredUpgrade> tieredUpgrade;
	private Price price;

	Upgrades(ItemStack displayItem, TieredUpgrade... upgrades) {
		this.displayItem = displayItem;
		this.tieredUpgrade = Arrays.asList(upgrades);
	}
	Upgrades() {

	}
	Upgrades(ItemStack displayItem, Price price) {
		this.displayItem = displayItem;
		this.price = price;
	}

	public ItemStack asShopItem(Team team) {
		ItemStack item = displayItem.clone();
		ItemMeta meta = item.getItemMeta();
		meta.setLore(addLore(team));
		item.setItemMeta(meta);
		return item;
	}
	public Price getPrice() {
		return price;
	}

	public List<TieredUpgrade> getTieredUpgrades() {
		return tieredUpgrade;
	}

	public List<String> addLore(Team team) {
		List<String> lore = new ArrayList<>();
		if (tieredUpgrade != null) {
			for (int i = 0; i < tieredUpgrade.size(); i++) {
				StringBuilder stringified = new StringBuilder(tieredUpgrade.get(i).getPrice().getMaterial().name().toLowerCase(Locale.ROOT));
				if (tieredUpgrade.get(i).getPrice().getValue() >= 2) {
					stringified.append("s");
				}
				lore.add(ChatColor.GRAY + "Tier " + (i + 1) + ": " + tieredUpgrade.get(i).getPrice().getValue() + " " + stringified);
			}
		} else {
			String stringified = price.getMaterial().name().toLowerCase(Locale.ROOT);
			if (price.getValue() >= 2) {
				stringified += "s";
			}
			lore.add(ChatColor.GRAY + "Price: " + price.getValue() + " " + stringified);
		}

		AtomicReference<BaseData> data = null;
		NovaBedwars.getInstance().getGame().getBases().forEach(baseData -> {
			if (baseData.getOwner().equals(team)) {
				data.set(baseData);
			}
		});
		int currentTier = 0;
		if (this == Upgrades.FORGE) {
			currentTier = data.get().getForgeLevel();
		} else if (this == Upgrades.PROTECTION) {
			currentTier = data.get().getProtectionLevel();
		}
		if (this == Upgrades.SHARPNESS) {
			if (data.get().hasSharpness()) {
				lore.set(0, ChatColor.GREEN + lore.get(0));
			}
		} else {
			for (int i = 0; i < currentTier; i++) {
				lore.set(i, ChatColor.GREEN + lore.get(i));
			}
		}
		return lore;
	}

}