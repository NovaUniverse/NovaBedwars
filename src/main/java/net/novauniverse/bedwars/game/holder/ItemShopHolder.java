package net.novauniverse.bedwars.game.holder;

import net.novauniverse.bedwars.game.enums.ItemCategory;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;

public class ItemShopHolder extends GUIReadOnlyHolder {
	private ItemCategory category;

	public ItemShopHolder(ItemCategory category) {
		this.category = category;
	}

	public ItemCategory getCategory() {
		return category;
	}
}