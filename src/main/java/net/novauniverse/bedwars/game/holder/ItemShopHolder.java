package net.novauniverse.bedwars.game.holder;

import net.novauniverse.bedwars.game.enums.ItemCategory;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIHolder;

public class ItemShopHolder extends GUIHolder {
    private ItemCategory category;

    public ItemShopHolder(ItemCategory category) {
        this.category = category;
    }
    public ItemCategory getCategory() {
        return category;
    }


}
