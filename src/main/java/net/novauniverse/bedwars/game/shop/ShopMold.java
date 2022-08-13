package net.novauniverse.bedwars.game.shop;

import net.novauniverse.bedwars.game.enums.ItemCategory;
import org.bukkit.entity.Player;

import java.io.IOException;

public abstract class ShopMold {

    abstract void display(ItemCategory category, Player player) throws IOException;

}
