package net.novauniverse.bedwars.utils;

import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

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

	public PotionItemBuilder setPotionEffect(PotionEffect effect) {
		if (item.getType() == Material.POTION) {
			PotionMeta meta = (PotionMeta) this.meta;
			meta.addCustomEffect(effect, true);
			meta.setMainEffect(effect.getType());
		}
		return this;
	}
}