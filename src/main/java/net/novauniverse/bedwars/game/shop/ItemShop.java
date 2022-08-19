package net.novauniverse.bedwars.game.shop;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.enums.ItemCategory;
import net.novauniverse.bedwars.game.enums.Items;
import net.novauniverse.bedwars.game.enums.Reason;
import net.novauniverse.bedwars.game.events.AttemptItemBuyEvent;
import net.novauniverse.bedwars.game.holder.ItemShopHolder;
import net.novauniverse.bedwars.game.modules.BedwarsPreferenceManager;
import net.novauniverse.bedwars.game.modules.BedwarsPreferences;
import net.novauniverse.bedwars.game.object.Price;
import net.novauniverse.bedwars.utils.BedwarsTextures;
import net.novauniverse.bedwars.utils.InventoryUtils;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemShop {
	public static final int IMPORT_HYPIXEL_PREFERENCES_SLOT = 49;

	public void display(Player player) {
		this.display(ItemCategory.QUICK_BUY, player);
	}

	public void display(ItemCategory category, Player player) {
		ItemShopHolder holder = new ItemShopHolder(category);
		Inventory inventory = Bukkit.getServer().createInventory(holder, 6 * 9, BedwarsNPC.ITEM_SHOP_NAME);
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
			BedwarsPreferences preferences = BedwarsPreferenceManager.getInstance().getPlayerPreferences(player);
			preferences.getItems().forEach(items -> {
				if (items != null) {
					if (!items.equals(Items.NO_ITEM)) {
						if (items.isTiered()) {
							int tier = 0;
							if (items == Items.WOOD_PICKAXE) {
								tier = NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) + 1;
							} else if (items == Items.WOOD_AXE) {
								tier = NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) + 1;
							}
							inventory.addItem(items.asShopItem(tier));
						} else {
							inventory.addItem(items.asShopItem());

						}
					}
				}
			});

			InventoryUtils.slotsWith(inventory, null).forEach(integer -> inventory.setItem(integer, Items.NO_ITEM.asShopItem()));

			if (NovaBedwars.getInstance().hasHypixelAPI()) {
				ItemBuilder builder = ItemBuilder.getPlayerSkullWithBase64TextureAsBuilder(BedwarsTextures.IMPORT_HYPIXEL_PREFERENCES_BUTTON);
				builder.setName(ChatColor.GOLD + "Import hypixel preferences");
				builder.addLore(ChatColor.AQUA + "This will try to import your bedwars preferences from hypixel");
				builder.setAmount(1);
				if (!BedwarsPreferenceManager.getInstance().isHypixelRequestCooldownActive(player)) {
					inventory.setItem(ItemShop.IMPORT_HYPIXEL_PREFERENCES_SLOT, builder.build());
				} else {
					Task task = new SimpleTask(NovaBedwars.getInstance(), () -> {
						ItemBuilder cooldown = new ItemBuilder(VersionIndependentUtils.getInstance().getColoredItem(DyeColor.RED, ColoredBlockType.GLASS_PANE)).setName(ChatColor.RED.toString() + ChatColor.BOLD + "Please wait " + BedwarsPreferenceManager.getInstance().getCooldown(player) + " seconds before importing again").setAmount(1);
						inventory.setItem(IMPORT_HYPIXEL_PREFERENCES_SLOT, cooldown.build());
						player.updateInventory();
					}, 1);
					task.start();
				}

				holder.addClickCallback(IMPORT_HYPIXEL_PREFERENCES_SLOT, (clickedInventory, inventory1, entity, clickedSlot, slotType, clickType) -> {
					player.performCommand("importhypixelpreferences");
					return GUIAction.ALLOW_INTERACTION;
				});
			}
		} else if (category == ItemCategory.COMBAT) {
			ItemStack defaultBow = new ItemBuilder(Material.BOW).setName(ChatColor.GOLD + "Bows").setAmount(1).build();
			ItemStack defaultSword = new ItemBuilder(VersionIndependentMaterial.GOLDEN_SWORD).setName(ChatColor.YELLOW + "Swords").setAmount(1).build();
			ItemStack defaultArmor = new ItemBuilder(Material.LEATHER_CHESTPLATE).setName(ChatColor.WHITE + "Armor").setAmount(1).build();
			inventory.setItem(19, defaultSword);
			inventory.setItem(20, blackbg);

			List<Items> swordList = Arrays.stream(Items.values()).filter(i -> i.asShopItem().getType().name().contains("SWORD") || i.asShopItem().getType() == Material.STICK).collect(Collectors.toList());
			List<Items> bowList = Arrays.stream(Items.values()).filter(i -> i.asShopItem().getType() == Material.BOW || i.asShopItem().getType() == Material.ARROW).collect(Collectors.toList());
			List<Items> armorList = Arrays.stream(Items.values()).filter(i -> i.asShopItem().getType().name().contains("BOOTS")).collect(Collectors.toList());

			for (int i = 21; i <= 26; i++) {
				try {
					inventory.setItem(i, swordList.get(i - 21).asShopItem());
				} catch (IndexOutOfBoundsException e) {
					inventory.setItem(i, bg);
				}
			}
			inventory.setItem(28, defaultArmor);
			inventory.setItem(29, blackbg);
			for (int i = 30; i <= 34; i++) {
				try {
					inventory.setItem(i, armorList.get(i - 30).asShopItem());
				} catch (IndexOutOfBoundsException e) {
					inventory.setItem(i, bg);
				}
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
					if (items.isTiered()) {
						int tier = 0;
						if (items == Items.WOOD_PICKAXE) {
							tier = NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) + 1;
						} else if (items == Items.WOOD_AXE) {
							tier = NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) + 1;
						}
						inventory.addItem(items.asShopItem(tier));
					} else {
						inventory.addItem(items.asShopItem());

					}
				}
			});
		}
		holder.addClickCallback(e -> {
			if (ItemCategory.isItemCategory(e.getCurrentItem())) {
				ItemCategory itemCategory = ItemCategory.toItemCategoryEnum(e.getCurrentItem());
				this.display(itemCategory, (Player) e.getWhoClicked());

			} else if (Items.isItemShopItem(e.getCurrentItem())) {
				Player p = (Player) e.getWhoClicked();

				Items item = Items.toItemEnum(e.getCurrentItem());
				if (item == Items.NO_ITEM) {
					return GUIAction.CANCEL_INTERACTION;
				}

				if (Price.canBuy(p, item)) {
					Price.buyItem(item, e.getWhoClicked().getInventory(), e.getCurrentItem(), p);
				} else {
					Bukkit.getPluginManager().callEvent(new AttemptItemBuyEvent(item, player, false, Reason.NOT_ENOUGHT_MATERIALS));
					return GUIAction.CANCEL_INTERACTION;
				}

			} else {
				// e.getWhoClicked().sendMessage(ChatColor.RED + "Fail: not shop item");
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