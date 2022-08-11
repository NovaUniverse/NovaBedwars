package net.novauniverse.bedwars.game.holder;

import net.novauniverse.bedwars.game.enums.ItemCategory;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.module.modules.gui.callbacks.GUIClickCallback;
import net.zeeraa.novacore.spigot.module.modules.gui.callbacks.GUIClickCallbackWithEvent;
import net.zeeraa.novacore.spigot.module.modules.gui.callbacks.GUICloseCallback;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIHolder;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ItemShopHolder extends GUIReadOnlyHolder {
	private final ItemCategory category;

	public ItemShopHolder(ItemCategory category) {
		this.category = category;
	}

	public ItemCategory getCategory() {
		return category;

	}
}