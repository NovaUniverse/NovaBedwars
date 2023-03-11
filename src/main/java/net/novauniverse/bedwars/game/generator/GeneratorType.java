package net.novauniverse.bedwars.game.generator;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum GeneratorType {
	IRON(Material.IRON_INGOT, ChatColor.WHITE, "Iron", 48), GOLD(Material.GOLD_INGOT, ChatColor.GOLD, "Gold", 8), DIAMOND(Material.DIAMOND, ChatColor.AQUA, "Diamond", 4), EMERALD(Material.EMERALD, ChatColor.GREEN, "Emerald", 2);

	private Material material;
	private ChatColor color;
	private String name;
	private int maxItems;

	private GeneratorType(Material material, ChatColor color, String name, int maxItems) {
		this.material = material;
		this.color = color;
		this.name = name;
		this.maxItems = maxItems;
	}

	public Material getMaterial() {
		return material;
	}

	public ChatColor getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	public int getMaxItems() {
		return maxItems;
	}
}