package net.novauniverse.bedwars.utils;

import net.novauniverse.bedwars.game.shop.ItemShop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryUtils {


    public static void removeItem(Inventory inventory, ItemStack stack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (item != null && item.equals(stack)) {
                inventory.setItem(i, null);
                break;
            }
        }
    }

    public static List<Integer> slotsWith(Inventory inventory, Collection<Material> materials) {
        ArrayList<Integer> slots = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (materials != null) {
                if (inventory.getItem(i) != null) {
                    for (Material material: materials) {
                        if (inventory.getItem(i).getType() == material) {
                            slots.add(i);
                        }
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
    public static List<Integer> slotsWith(Inventory inventory, Material... materials) {
        ArrayList<Integer> slots = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (materials != null) {
                if (inventory.getItem(i) != null) {
                    for (Material material: materials) {
                        if (inventory.getItem(i).getType() == material) {
                            slots.add(i);
                        }
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
    public static List<Integer> slotsWith(Inventory inventory, Material material) {
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

    public static boolean hasOnCursor(Player player, Material... materials) {
        return Arrays.stream(materials).collect(Collectors.toList()).contains(player.getItemOnCursor().getType());
    }

    public static boolean hasOnCursor(Player player, Material material) {
        return material == player.getItemOnCursor().getType();
    }

}