package net.novauniverse.bedwars.game.holder;

import net.md_5.bungee.api.chat.TextComponent;
import net.novauniverse.bedwars.game.enums.ItemCategory;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;

public class ItemShopHolder extends GUIReadOnlyHolder {
	private final ItemCategory category;

	public ItemShopHolder(ItemCategory category) {
		this.category = category;
	}

	public ItemCategory getCategory() {
		return category;
	}

}