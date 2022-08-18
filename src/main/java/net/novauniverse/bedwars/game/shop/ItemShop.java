package net.novauniverse.bedwars.game.shop;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.enums.ItemCategory;
import net.novauniverse.bedwars.game.enums.Items;
import net.novauniverse.bedwars.game.holder.ItemShopHolder;
import net.novauniverse.bedwars.game.modules.BedwarsPreferenceManager;
import net.novauniverse.bedwars.game.modules.BedwarsPreferences;
import net.novauniverse.bedwars.game.modules.PreferenceAPIRequestCallback;
import net.novauniverse.bedwars.game.object.Price;
import net.novauniverse.bedwars.utils.BedwarsTextures;
import net.novauniverse.bedwars.utils.InventoryUtils;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.module.modules.gui.callbacks.GUIClickCallback;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemShop {
	public static final int IMPORT_HYPIXEL_PREFERENCES_SLOT = 53;

	public void display(Player player) {
		this.display(ItemCategory.QUICK_BUY, player);
	}

	@SuppressWarnings("null")
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
				if (items != null || items.equals(Items.NO_ITEM)) {
					inventory.addItem(items.asShopItem());
				}
			});

			InventoryUtils.slotsWith(inventory, null).forEach(integer -> inventory.setItem(integer, Items.NO_ITEM.asShopItem()));

			if (NovaBedwars.getInstance().hasHypixelAPI()) {
				ItemBuilder builder = ItemBuilder.getPlayerSkullWithBase64TextureAsBuilder(BedwarsTextures.IMPORT_HYPIXEL_PREFERENCES_BUTTON);
				builder.setName(ChatColor.GOLD + "Import hypixel preferences");
				builder.addLore(ChatColor.AQUA + "This will try to import your bedwars preferences from hypixel");
				builder.setAmount(1);

				inventory.setItem(ItemShop.IMPORT_HYPIXEL_PREFERENCES_SLOT, builder.build());
				holder.addClickCallback(IMPORT_HYPIXEL_PREFERENCES_SLOT, new GUIClickCallback() {
					@Override
					public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
						if (BedwarsPreferenceManager.getInstance().isHypixelRequestCooldownActive(player)) {
							player.sendMessage(ChatColor.RED + "Please wait " + BedwarsPreferenceManager.getInstance().getCooldown(player) + " seconds before trying to download preferences again");
						} else {
							if (!BedwarsPreferenceManager.getInstance().tryImportHypixelPreferences(player, new PreferenceAPIRequestCallback() {
								@Override
								public void onResult(boolean success, Exception exception) {
									if (success) {
										player.closeInventory();
										VersionIndependentSound.NOTE_PLING.play(player);
										player.sendMessage(ChatColor.GREEN + "Hypixel preferences imported");
									} else {
										if (exception == null) {
											player.closeInventory();
											VersionIndependentSound.NOTE_PLING.play(player);
											player.sendMessage(ChatColor.RED + "Could not import your hypixel preferences. this could be caused by you changing your minecraft name recently or an error on our side");
										} else {
											player.closeInventory();
											player.sendMessage(ChatColor.DARK_RED + "An error occured while trying to import your preferences. " + exception.getClass().getName());
											Log.error("ItemShop", "Failed to import preferences for player " + player.getName() + ". " + exception.getClass().getName() + " " + exception.getMessage());
											exception.printStackTrace();
										}
									}
								}
							})) {
								player.sendMessage(ChatColor.DARK_RED + "Import failure");
							}
						}
						return GUIAction.CANCEL_INTERACTION;
					}
				});
			}
		} else if (category == ItemCategory.COMBAT) {
			ItemStack defaultBow = new ItemBuilder(Material.BOW).setName(ChatColor.GOLD + "Bows").setAmount(1).build();
			ItemStack defaultSword = new ItemBuilder(VersionIndependentMaterial.GOLDEN_SWORD).setName(ChatColor.YELLOW + "Swords").setAmount(1).build();
			ItemStack defaultArmor = new ItemBuilder(Material.LEATHER_CHESTPLATE).setName(ChatColor.WHITE + "Armor").setAmount(1).build();
			inventory.setItem(19, defaultSword);
			inventory.setItem(20, blackbg);

			List<Items> swordList = Arrays.stream(Items.values()).filter(i -> i.asShopItem().getType().name().contains("SWORD") || i.asShopItem().getType() == Material.STICK).collect(Collectors.toList());
			List<Items> bowList = Arrays.stream(Items.values()).filter(i -> i.asShopItem().getType() == Material.BOW).collect(Collectors.toList());
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
					if (!items.isTiered()) {

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

				Items item = Items.toItemEnum(e.getCurrentItem());
				if (Price.canBuy((Player) e.getWhoClicked(), item)) {
					Price.buyItem(item, e.getWhoClicked().getInventory(), e.getCurrentItem(), (Player) e.getWhoClicked());
				} else {
					e.getWhoClicked().sendMessage(ChatColor.RED + "Fail: not enough materials");
					return GUIAction.CANCEL_INTERACTION;
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