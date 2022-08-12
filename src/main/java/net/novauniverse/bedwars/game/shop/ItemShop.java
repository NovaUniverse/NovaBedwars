package net.novauniverse.bedwars.game.shop;

import com.google.gson.stream.JsonReader;
import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.enums.ItemCategory;
import net.novauniverse.bedwars.game.holder.ItemShopHolder;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class ItemShop extends ShopMold {
    @Override
    public void display(ItemCategory category) {
        ItemShopHolder holder = new ItemShopHolder(category);
        Inventory inventory = Bukkit.getServer().createInventory(holder, 9 * 6, BedwarsNPC.ITEM_SHOP_NAME);
        ItemStack bg = new ItemBuilder(VersionIndependentUtils.get().getColoredItem(DyeColor.GRAY, ColoredBlockType.GLASS_PANE)).setName("").build();
        for(int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i,bg);
        }
        inventory.setItem(category.getSlot(), category.asSelectedItem());
        placeRemainingOnes(inventory, bg);
        for (int i = 19; i < 230; i++) {
        }
    }


        public void placeRemainingOnes(Inventory inventory, ItemStack defaultItem) {
        if (inventory.getItem(0).getItemMeta().equals(defaultItem.getItemMeta())) {
            inventory.setItem(0, ItemCategory.QUICK_BUY.asItem());
        }
        if (inventory.getItem(2).getItemMeta().equals(defaultItem.getItemMeta())) {
            inventory.setItem(2, ItemCategory.COMBAT.asItem());
        }
        if (inventory.getItem(3).getItemMeta().equals(defaultItem.getItemMeta())) {
            inventory.setItem(3,ItemCategory.BLOCK.asItem());
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
