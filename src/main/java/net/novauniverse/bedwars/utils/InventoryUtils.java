package net.novauniverse.bedwars.utils;

import net.novauniverse.bedwars.game.enums.ItemCategory;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class InventoryUtils {


    public static ArrayList<Integer> slotsWith(Inventory inventory, Material material) {
        ArrayList<Integer> slots = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (material != null) {
                if (inventory.getItem(i) != null) {
                    if (inventory.getItem(i).getType() == material) {
                        slots.add(i);
                    }
                }

            } else {
                if (inventory.getItem(i) == null) {
                    slots.add(i);
                }
            }

        }
        return slots;
    }
}
