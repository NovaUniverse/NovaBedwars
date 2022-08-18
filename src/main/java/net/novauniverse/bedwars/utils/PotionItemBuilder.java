package net.novauniverse.bedwars.utils;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class 	PotionItemBuilder extends ItemBuilder {

	public PotionItemBuilder(Material material) {
		super(material);
	}

	public PotionItemBuilder(Material material, int ammount) {
		super(material, ammount);
	}

	public PotionItemBuilder(VersionIndependentMaterial material) {
		super(material);
	}

	public PotionItemBuilder(VersionIndependentMaterial material, int ammount) {
		super(material, ammount);
	}

	public PotionItemBuilder(ItemStack itemStack) {
		super(itemStack);
	}

	public PotionItemBuilder(ItemStack itemStack, boolean clone) {
		super(itemStack, clone);
	}


	public PotionItemBuilder setPotionEffect(PotionEffect effect, boolean color) {
		if (item.getType() == Material.POTION) {
			PotionMeta meta = (PotionMeta) this.meta;
			meta.addCustomEffect(effect, false);
			if (color) {
				try {
					item.setDurability((short) getFromEffectType(effect.getType()).getDamageValue());
				} catch (TypeNotPresentException e) {
					Log.warn("Could not set item color, defaulting to water bottle.");
				}
			}
			this.meta = meta;
		}
		return this;
	}
	private PotionType getFromEffectType(PotionEffectType type) {
		PotionType potionType = null;
		for (PotionType potType : PotionType.values()) {
			if (potType.toString().equalsIgnoreCase(type.getName())) {
				potionType = potType;
			}
		}
		if (potionType == null) {
			throw new TypeNotPresentException("Could not find PotionType for " + type.toString(), new Throwable());
		}
		return potionType;
	}

}