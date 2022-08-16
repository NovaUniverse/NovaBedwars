package net.novauniverse.bedwars.game.shop;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.enums.ItemCategory;
import net.novauniverse.bedwars.game.enums.Items;
import net.novauniverse.bedwars.game.holder.ItemShopHolder;
import net.novauniverse.bedwars.game.modules.BedwarsPreferenceManager;
import net.novauniverse.bedwars.game.modules.BedwarsPreferences;
import net.novauniverse.bedwars.game.object.Price;
import net.novauniverse.bedwars.game.object.TieredItem;
import net.novauniverse.bedwars.utils.InventoryUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.module.modules.gui.callbacks.GUIClickCallbackWithEvent;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemShop extends ShopMold {
    @SuppressWarnings("null")
	@Override
    public void display(ItemCategory category, Player player) throws IOException {
        ItemShopHolder holder = new ItemShopHolder(category);
        Inventory inventory = Bukkit.getServer().createInventory(holder, 9 * 6, BedwarsNPC.ITEM_SHOP_NAME);
        ItemStack bg = new ItemBuilder(VersionIndependentUtils.get().getColoredItem(DyeColor.GRAY, ColoredBlockType.GLASS_PANE)).setName(" ").setAmount(1).build();
        ItemStack blackbg = new ItemBuilder(VersionIndependentUtils.get().getColoredItem(DyeColor.BLACK, ColoredBlockType.GLASS_PANE)).setName(" ").setAmount(1).build();
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

        if (category == ItemCategory.QUICK_BUY) {
            if (!BedwarsPreferenceManager.getInstance().tryImportHypixelPreferences(player, (success, exception) -> {
                if (!success) {
                    if (exception != null) {
                        player.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Fetch retured false. Exception: " + exception.getClass().getName() + " " + exception.getMessage());
                        exception.printStackTrace();
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Fetch retured false. No exception");
                    }
                } else {
                    player.sendMessage(ChatColor.GREEN + "OK>" + ChatColor.WHITE + " Import success");

                    if (!BedwarsPreferenceManager.getInstance().savePreferences(player, (success1, exception1) -> {
                        if (!success1) {
                            if (exception1 != null) {
                                player.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Save retured false. Exception: " + exception1.getClass().getName() + " " + exception1.getMessage());
                                exception1.printStackTrace();
                            } else {
                                player.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Save retured false. No exception");
                            }
                        } else {
                            player.sendMessage(ChatColor.GREEN + "OK>" + ChatColor.WHITE + " Export success");
                        }
                    })) {
                        player.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Save method returned false");
                    }
                }
            })) {
                player.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Fetch method returned false");
            }
            BedwarsPreferences preferences = new BedwarsPreferences(player.getUniqueId(), BedwarsPreferences.parseItems(NovaBedwars.getInstance().getPreferenceAPI().getPreferences(player)));
            preferences.getItems().forEach(items -> {
                if (items != null || items.equals(Items.NO_ITEM)) {
                    inventory.addItem(items.asShopItem());
                }
            });

            InventoryUtils.slotsWith(inventory, null).forEach(integer -> inventory.setItem(integer, Items.NO_ITEM.asShopItem()));

        } else if (category == ItemCategory.COMBAT) {
            ItemStack defaultBow = new ItemBuilder(Material.BOW).setName(ChatColor.GOLD + "Bows").setAmount(1).build();
            ItemStack defaultSword = new ItemBuilder(VersionIndependentMaterial.GOLDEN_SWORD).setName(ChatColor.GRAY + "Swords").setAmount(1).build();
            inventory.setItem(19, defaultSword);
            inventory.setItem(20, blackbg);

            List<Items> swordList = Arrays.stream(Items.values()).filter(i -> i.asShopItem().getType().name().contains("SWORD") || i.asShopItem().getType() == Material.STICK).collect(Collectors.toList());
            List<Items> bowList = Arrays.stream(Items.values()).filter(i -> i.asShopItem().getType() == Material.BOW).collect(Collectors.toList());
            
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
        holder.addClickCallback(e -> {
           if (Items.isItemShopItem(e.getCurrentItem())) {
               Items item = Items.toItemEnum(e.getCurrentItem());
              if (Price.canBuy((Player) e.getWhoClicked(), item)) {
                          if (InventoryUtils.slotsWith(e.getClickedInventory(), null).size() != 0) {
                              e.getWhoClicked().sendMessage(ChatColor.RED + "Fail: no slots available");
                              return GUIAction.CANCEL_INTERACTION;
                          }
                          if (item.isArmor()) {
                              if (NovaBedwars.getInstance().getAllPlayerArmor().get((Player) e.getWhoClicked()) == item.getArmorType()) {
                                  e.getWhoClicked().sendMessage(ChatColor.RED + "Fail: already have armor");
                                  return GUIAction.CANCEL_INTERACTION;
                              }
                              NovaBedwars.getInstance().getAllPlayerArmor().putIfAbsent((Player) e.getWhoClicked(), item.getArmorType());
                              NovaBedwars.getInstance().getAllPlayerArmor().put((Player) e.getWhoClicked(), item.getArmorType());
                              e.getWhoClicked().sendMessage(ChatColor.GREEN + "Success: armor bought");
                              return GUIAction.ALLOW_INTERACTION;
                          } else if (item.isTiered()) {

                              if (item == Items.WOOD_PICKAXE) {

                                  for (int i = 0; i <= item.getTieredItems().size(); i++)  {

                                      if (i == item.getTieredItems().size()) {
                                          e.getWhoClicked().sendMessage(ChatColor.RED + "Fail: tier limit reached");
                                          return GUIAction.CANCEL_INTERACTION;
                                      }

                                      if (e.getCurrentItem().equals(item.getTieredItems().get(i))) {
                                          NovaBedwars.getInstance().getAllPlayersPickaxeTier().put((Player) e.getWhoClicked(), i + 1);
                                          NovaBedwars.getInstance().getAllPlayersPickaxeTier().put((Player) e.getWhoClicked(), i + 1);
                                          e.getWhoClicked().sendMessage(ChatColor.GREEN + "Success: new pickaxe tier bought");
                                          return GUIAction.ALLOW_INTERACTION;
                                      }

                                  }
                              } else if (item == Items.WOOD_AXE) {

                                  for (int i = 0; i <= item.getTieredItems().size(); i++)  {

                                      if (i == item.getTieredItems().size()) {
                                          e.getWhoClicked().sendMessage(ChatColor.RED + "Fail: tier limit reached");
                                          return GUIAction.CANCEL_INTERACTION;
                                      }

                                      if (e.getCurrentItem().equals(item.getTieredItems().get(i))) {
                                          NovaBedwars.getInstance().getAllPlayersAxeTier().put((Player) e.getWhoClicked(), i + 1);
                                          NovaBedwars.getInstance().getAllPlayersAxeTier().putIfAbsent((Player) e.getWhoClicked(), i + 1);
                                          e.getWhoClicked().sendMessage(ChatColor.GREEN + "Success: new axe tier bought");
                                          return GUIAction.ALLOW_INTERACTION;
                                      }

                                  }
                              }
                          } else {
                              e.getWhoClicked().getInventory().addItem(item.getItemStack());
                              e.getWhoClicked().sendMessage(ChatColor.GREEN + "Success: normal item bought");
                              return GUIAction.ALLOW_INTERACTION;

                          }

              } else {
                  e.getWhoClicked().sendMessage(ChatColor.RED + "Fail: not enough materials");
              }

           } else {
               e.getWhoClicked().sendMessage(ChatColor.RED + "Fail: not shop item");
               return GUIAction.CANCEL_INTERACTION;
           }
            return GUIAction.NONE;
        });
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