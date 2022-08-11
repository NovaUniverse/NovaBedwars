package net.novauniverse.bedwars.game.enums;

import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ItemCategory {
	COMBAT("Combat", ChatColor.RED, VersionIndependentMaterial.GOLDEN_SWORD.toBukkitVersion()), BLOCK("Blocks", ChatColor.GOLD, Material.BRICK), MISC("Miscelaneous (please tell me how to type that)",ChatColor.GRAY, Material.FIREBALL),
	TOOLS("Tools",ChatColor.DARK_AQUA, Material.IRON_PICKAXE), POTIONS("Potions",ChatColor.LIGHT_PURPLE), QUICK_BUY("Quick Buy", ChatColor.WHITE);
	private String iconName;
	ItemCategory(String name, ChatColor color, Material icon) {
		ItemStack itemStack = new ItemStack(Material.STICK);
		itemStack.setTypeId();
	}
}