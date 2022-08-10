package net.novauniverse.bedwars.game.enums;

import net.novauniverse.bedwars.utils.PotionItemBuilder;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum Items {
	WOOL(Material.WOOL, ColoredBlockType.WOOL, 16, ItemCategory.BLOCK),
	CLAY(Material.STAINED_CLAY, ColoredBlockType.CLAY, 16, ItemCategory.BLOCK),
	ENDSTONE(VersionIndependentMaterial.END_STONE, 12, ItemCategory.BLOCK),
	LADDER(Material.LADDER, 6, ItemCategory.BLOCK),
	WOOD(Material.WOOD, 16, ItemCategory.BLOCK),
	OBSIDIAN(Material.OBSIDIAN, 4, ItemCategory.BLOCK),
	WOODEN_SWORD(VersionIndependentMaterial.WOODEN_SWORD, 1, ItemCategory.COMBAT),
	STONE_SWORD(Material.STONE_SWORD, 1, ItemCategory.COMBAT),
	IRON_SWORD(Material.IRON_SWORD, 1, ItemCategory.COMBAT),
	DIAMOND_SWORD(Material.DIAMOND_SWORD, 1, ItemCategory.COMBAT),
	KB_STICK(Material.STICK, 1, ItemCategory.COMBAT),
	GOLD_ARMOR(ArmorType.GOLD, ItemCategory.COMBAT),
	CHAINMAIL_ARMOR(ArmorType.CHAINMAIL, ItemCategory.COMBAT),
	IRON_ARMOR(ArmorType.IRON, ItemCategory.COMBAT),
	DIAMOND_ARMOR(ArmorType.DIAMOND, ItemCategory.COMBAT),
	SHEARS(Material.SHEARS, 1, ItemCategory.TOOLS),
	WOOD_PICKAXE(VersionIndependentMaterial.WOODEN_PICKAXE, 1, ItemCategory.TOOLS),
	STONE_PICKAXE(Material.STONE_PICKAXE, 1, ItemCategory.TOOLS),
	IRON_PICKAXE(Material.IRON_PICKAXE, 1, ItemCategory.TOOLS),
	GOLD_PICKAXE(VersionIndependentMaterial.GOLDEN_PICKAXE, 1, ItemCategory.TOOLS),
	DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE, 1, ItemCategory.TOOLS),
	WOOD_AXE(VersionIndependentMaterial.WOODEN_AXE, 1, ItemCategory.TOOLS),
	STONE_AXE(Material.STONE_AXE, 1, ItemCategory.TOOLS),
	IRON_AXE(Material.IRON_AXE, 1, ItemCategory.TOOLS),
	GOLD_AXE(Material.GOLD_AXE, 1, ItemCategory.TOOLS),
	DIAMOND_AXE(Material.DIAMOND_AXE, 1, ItemCategory.TOOLS),
	ARROW(Material.ARROW, 8, ItemCategory.COMBAT),
	BOW(Material.BOW, 1, ItemCategory.COMBAT),
	BOW_POWER_1(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 1).build(), ItemCategory.COMBAT),
	BOW_PUNCH_1_POW_1(new ItemBuilder(Material.BOW).addEnchant(Enchantment.DAMAGE_ALL, 1).addEnchant(Enchantment.ARROW_KNOCKBACK, 1).build(), ItemCategory.COMBAT),
	INVISIBLE(new PotionItemBuilder(Material.POTION).setPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30, 0, false, false)).build(), ItemCategory.POTIONS),
	JUMP_BOOST(new PotionItemBuilder(Material.POTION).setPotionEffect(new PotionEffect(PotionEffectType.JUMP, 45, 4, false, false)).build(), ItemCategory.POTIONS),
	SPEED(new PotionItemBuilder(Material.POTION).setPotionEffect(new PotionEffect(PotionEffectType.SPEED, 45, 1, false, false)).build(), ItemCategory.POTIONS),
	GOLDEN_APPLE(Material.GOLDEN_APPLE, 1, ItemCategory.MISC),
	FIREBALL(Material.FIREBALL, 1, ItemCategory.MISC),
	TNT(Material.TNT, 1, ItemCategory.MISC),
	ENDER_PEARL(Material.ENDER_PEARL, 1, ItemCategory.MISC),
	WATER_BUCKET(Material.WATER_BUCKET, 1, ItemCategory.MISC),
	SPONGE(Material.SPONGE, 2, ItemCategory.MISC);

	private ColoredBlockType coloredBlockType = null;
	private ArmorType armorType = null;
	private int amount = 0;
	private final ItemCategory category;
	private Material material = null;
	private ItemStack itemStack;

	private Items(Material material, ColoredBlockType colorMaterial, int amount, ItemCategory category) {
		this.coloredBlockType = colorMaterial;
		this.material = material;
		this.amount = amount;
		this.category = category;
		this.itemStack = new ItemStack(material, amount);
	}

	private Items(VersionIndependentMaterial material, int amount, ItemCategory category) {
		this(material.toBukkitVersion(), amount, category);
	}

	private Items(Material material, int amount, ItemCategory category) {
		this.material = material;
		this.amount = amount;
		this.category = category;
		this.itemStack = new ItemStack(material, amount);
	}

	private Items(ItemStack itemStack, ItemCategory category) {
		this.material = itemStack.getType();
		this.amount = itemStack.getAmount();
		this.category = category;
		this.itemStack = itemStack;
	}

	private Items(ArmorType type, ItemCategory category) {
		this.armorType = type;
		this.category = category;
	}

	public ColoredBlockType getColoredBlockType() {
		return coloredBlockType;
	}

	public ArmorType getArmorType() {
		return armorType;
	}

	public int getAmount() {
		return amount;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public Material getMaterial() {
		return material;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}
}