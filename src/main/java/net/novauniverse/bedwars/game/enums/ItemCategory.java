package net.novauniverse.bedwars.game.enums;

import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum ItemCategory {
	QUICK_BUY("Quick Buy", ChatColor.WHITE, Material.NETHER_STAR,0),COMBAT("Combat", ChatColor.RED, VersionIndependentMaterial.GOLDEN_SWORD.toBukkitVersion(),2),
	BLOCK("Blocks", ChatColor.GOLD, Material.BRICK,3),
	TOOLS("Tools",ChatColor.DARK_AQUA, Material.IRON_PICKAXE,4), POTIONS("Potions",ChatColor.LIGHT_PURPLE, Material.POTION,5),
	MISC("Miscellaneous",ChatColor.GRAY, Material.FIREBALL, 6);
	private final String iconName;
	private final ChatColor color;
	private final Material icon;
	private final int slot;
	ItemCategory(String name, ChatColor color, Material icon, int slot) {
		this.iconName = name;
		this.color = color;
		this.icon = icon;
		this.slot = slot;
	}
	public ItemStack asItem() {
		ItemStack item = new ItemStack(icon);
		if (this == POTIONS) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED,0,0,false), true);
			item.setItemMeta(meta);
		}
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color + iconName);
		item.setItemMeta(meta);
		return item;
	}
	public static boolean isItemCategory(ItemStack item) {
		boolean check = false;
		for (ItemCategory category : ItemCategory.values()) {
			if (item.toString().equalsIgnoreCase(category.asItem().toString())) {
				check = item.toString().equalsIgnoreCase(category.asItem().toString());
				break;
			}
		}
		return check;
	}

	public static ItemCategory toItemCategoryEnum(ItemStack item) {
		ItemCategory cat = null;
		for (ItemCategory category: ItemCategory.values()) {
			if (item.toString().equalsIgnoreCase(category.asItem().toString())) {
				cat = category;
				break;
			}

		}
		return cat;
	}


	public ItemStack asSelectedItem() {
 		ItemStack item = new ItemStack(VersionIndependentUtils.get().getColoredItem(DyeColor.LIME, ColoredBlockType.GLASS_PANE));
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GREEN + iconName);
		item.setItemMeta(meta);
		item.setAmount(1);
		return item;
	}
	public int getSlot() {
		return slot;
	}
	public String getIconName() {
		return iconName;
	}
	public ChatColor getColor() {
		return color;
	}
	public Material getIconMaterial() {
		return icon;
	}
}