package net.novauniverse.bedwars.game.shop;

import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.enums.ItemCategory;
import net.novauniverse.bedwars.game.enums.Items;
import net.novauniverse.bedwars.game.holder.ItemShopHolder;
import net.novauniverse.bedwars.game.object.ItemPreferences;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemShop extends ShopMold {
    @Override
    public void display(ItemCategory category, Player player) throws IOException {
        ItemShopHolder holder = new ItemShopHolder(category);
        Inventory inventory = Bukkit.getServer().createInventory(holder, 9 * 6, BedwarsNPC.ITEM_SHOP_NAME);
        ItemStack bg = new ItemBuilder(VersionIndependentUtils.get().getColoredItem(DyeColor.GRAY, ColoredBlockType.GLASS_PANE)).setName("").setAmount(1).build();
        ItemStack blackbg = new ItemBuilder(VersionIndependentUtils.get().getColoredItem(DyeColor.BLACK, ColoredBlockType.GLASS_PANE)).setName("").setAmount(1).build();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, bg);
        }
        inventory.setItem(category.getSlot(), category.asSelectedItem());
        placeRemainingOnes(inventory, bg);
        for (int i = 19; i <= 25; i++) {
            for (int j = 0; j <= 2; j++) {
                inventory.setItem(i + (j * 9), ItemBuilder.AIR);
            }
        }
        ItemStack emptySlot = new ItemBuilder(VersionIndependentUtils.getInstance().getColoredItem(DyeColor.RED, ColoredBlockType.GLASS_PANE)).setName(ChatColor.RED + "Empty Slot").setAmount(1).build();
        if (category == ItemCategory.QUICK_BUY) {
            ItemPreferences preferences = new ItemPreferences(player);

            preferences.itemsArray().forEach(items -> {
                if (items == null) {
                    inventory.addItem(emptySlot);
                } else {
                    inventory.addItem(items.asShopItem());
                }
            });
        } else if (category == ItemCategory.COMBAT) {
            ItemStack defaultBow = new ItemBuilder(Material.BOW).setName(ChatColor.GOLD + "Bows").setAmount(1).build();
            ItemStack defaultSword = new ItemBuilder(VersionIndependentMaterial.GOLDEN_SWORD).setName(ChatColor.GRAY + "Swords").setAmount(1).build();
            inventory.setItem(19, defaultSword);
            inventory.setItem(20, blackbg);
            //ArrayList<Items> swordList = new ArrayList<>(Arrays.stream(Items.values()).filter(items1 -> items1.asShopItem().getType().name().contains("SWORD") || items1.asShopItem().getType() == Material.STICK));
            //ArrayList<Items> bowList = new ArrayList<>(Arrays.stream(Items.values()).filter(items1 -> items1.asShopItem().getType() == Material.BOW));
            
            List<Items> swordList = new ArrayList<>(Arrays.stream(Items.values()).filter(i -> i.asShopItem().getType().name().contains("SWORD") || i.asShopItem().getType() == Material.STICK).collect(Collectors.toList()));
            List<Items> bowList = new ArrayList<>(Arrays.stream(Items.values()).filter(i -> i.asShopItem().getType() == Material.BOW).collect(Collectors.toList()));
            
            for (int i = 21; i <= 26; i++) {
                try {
                    inventory.setItem(i, swordList.get(i - 21).asShopItem());
                } catch (IndexOutOfBoundsException e) {
                    inventory.setItem(i, bg);
                }
            }
            for (int i = 28; i <= 34; i++) {
                inventory.setItem(i, blackbg);
            }
            for (int i = 39; i <= 43; i++) {
                try {
                    inventory.setItem(i, bowList.get(i - 39).asShopItem());
                } catch (IndexOutOfBoundsException e) {
                    inventory.setItem(i, bg);
                }
            }
            inventory.setItem(37, defaultBow);
            inventory.setItem(38, blackbg);
        } else {
            Arrays.stream(Items.values()).forEach(items -> {
                if (items.getCategory() == category) {
                    if (!items.isTiered()) {

                        inventory.addItem(items.asShopItem());
                    }
                }
            });
        }
        player.openInventory(inventory);
    }


    private void placeRemainingOnes(Inventory inventory, ItemStack defaultItem) {
        if (inventory.getItem(0).getItemMeta().equals(defaultItem.getItemMeta())) {
            inventory.setItem(0, ItemCategory.QUICK_BUY.asItem());
        }
        if (inventory.getItem(2).getItemMeta().equals(defaultItem.getItemMeta())) {
            inventory.setItem(2, ItemCategory.COMBAT.asItem());
        }
        if (inventory.getItem(3).getItemMeta().equals(defaultItem.getItemMeta())) {
            inventory.setItem(3, ItemCategory.BLOCK.asItem());
        }
        if (inventory.getItem(4).getItemMeta().equals(defaultItem.getItemMeta())) {
            inventory.setItem(4, ItemCategory.TOOLS.asItem());
        }
        if (inventory.getItem(5).getItemMeta().equals(defaultItem.getItemMeta())) {
            inventory.setItem(5, ItemCategory.POTIONS.asItem());
        }
        if (inventory.getItem(6).getItemMeta().equals(defaultItem.getItemMeta())) {
            inventory.setItem(6, ItemCategory.MISC.asItem());
        }
    }
}