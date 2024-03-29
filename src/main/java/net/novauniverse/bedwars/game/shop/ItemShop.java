package net.novauniverse.bedwars.game.shop;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.enums.ItemCategory;
import net.novauniverse.bedwars.game.enums.ShopItem;
import net.novauniverse.bedwars.game.holder.ItemShopHolder;
import net.novauniverse.bedwars.game.holder.ReplaceHolder;
import net.novauniverse.bedwars.game.modules.preferences.BedwarsPreferenceManager;
import net.novauniverse.bedwars.game.modules.preferences.BedwarsPreferences;
import net.novauniverse.bedwars.game.object.Price;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.novauniverse.bedwars.utils.BedwarsTextures;
import net.novauniverse.bedwars.utils.InventoryUtils;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class ItemShop {
	public static final int IMPORT_HYPIXEL_PREFERENCES_SLOT = 49;

	private Task task;

	public void updateHypixelAPIButton(Player player) {
		if (NovaBedwars.getInstance().hasHypixelAPI()) {
			Inventory inventory = player.getOpenInventory().getTopInventory();
			if (inventory.getHolder() instanceof ItemShopHolder) {
				ItemShopHolder holder = (ItemShopHolder) inventory.getHolder();
				if (holder.getCategory() == ItemCategory.QUICK_BUY) {
					if (!BedwarsPreferenceManager.getInstance().isHypixelRequestCooldownActive(player)) {
						ItemBuilder builder = ItemBuilder.getPlayerSkullWithBase64TextureAsBuilder(BedwarsTextures.IMPORT_HYPIXEL_PREFERENCES_BUTTON);
						builder.setName(ChatColor.GOLD + "Import hypixel preferences");
						builder.addLore(ChatColor.AQUA + "This will try to import your bedwars preferences from hypixel");
						builder.setAmount(1);
						inventory.setItem(ItemShop.IMPORT_HYPIXEL_PREFERENCES_SLOT, builder.build());
					} else {
						ItemBuilder cooldown = new ItemBuilder(VersionIndependentUtils.getInstance().getColoredItem(DyeColor.RED, ColoredBlockType.GLASS_PANE)).setName(ChatColor.RED.toString() + ChatColor.BOLD + "Please wait " + BedwarsPreferenceManager.getInstance().getCooldown(player) + " seconds before importing again").setAmount(1);
						inventory.setItem(IMPORT_HYPIXEL_PREFERENCES_SLOT, cooldown.build());
					}
				}

			}
		}
	}

	public ItemShop() {
		if (NovaBedwars.getInstance().hasHypixelAPI()) {
			task = new SimpleTask(() -> Bukkit.getServer().getOnlinePlayers().forEach(this::updateHypixelAPIButton), 5L);
			Task.tryStartTask(task);
		}
	}

	public void destroy() {
		Task.tryStopTask(task);
	}

	public void display(Player player) {
		this.display(ItemCategory.QUICK_BUY, player);
	}

	public void display(ItemCategory category, Player player) {
		ItemShopHolder holder = new ItemShopHolder(category);
		Inventory inventory = Bukkit.getServer().createInventory(holder, 6 * 9, BedwarsNPC.ITEM_SHOP_NAME);
		ItemStack bg = new ItemBuilder(VersionIndependentUtils.get().getColoredItem(DyeColor.GRAY, ColoredBlockType.GLASS_PANE)).setName(" ").setAmount(1).build();
		ItemStack blackbg = new ItemBuilder(VersionIndependentUtils.get().getColoredItem(DyeColor.BLACK, ColoredBlockType.GLASS_PANE)).setName(" ").setAmount(1).build();
		BedwarsPreferences preferences = BedwarsPreferenceManager.getInstance().getPlayerPreferences(player);
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

		holder.addClickCallback(e -> {

			if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
				if (!ItemCategory.isItemCategory(e.getCurrentItem())) {
					if (ShopItem.isItemShopItem(e.getCurrentItem())) {

						if (category == ItemCategory.QUICK_BUY) {
							ShopItem item = ShopItem.toItemEnum(e.getCurrentItem());
							preferences.removeItem(item);
							display(ItemCategory.QUICK_BUY, player);
						} else {
							ShopItem replacing = ShopItem.toItemEnum(e.getCurrentItem());
							ReplaceHolder rh = new ReplaceHolder();
							Inventory replace = Bukkit.createInventory(rh, 27, ChatColor.YELLOW + "Which slot?");
							replace.setItem(0, bg);
							replace.setItem(8, bg);
							replace.setItem(9, bg);
							replace.setItem(17, bg);
							replace.setItem(18, bg);
							replace.setItem(26, bg);
							List<Integer> slots = InventoryUtils.slotsWith(replace, (Collection<Material>) null);
							ListIterator<ShopItem> listerator = preferences.getItems().listIterator();
							slots.forEach(slot -> {
								if (listerator.hasNext()) {
									ShopItem next = listerator.next();
									replace.setItem(slot, next == null ? ShopItem.NO_ITEM.asDisplayItem() : next.asDisplayItem());
								} else {
									replace.setItem(slot, ShopItem.NO_ITEM.asDisplayItem());
								}
							});

							rh.addClickCallback(e1 -> {
								if (e1.getCurrentItem() == null) {
									return GUIAction.NONE;
								}
								if (ShopItem.isDisplayItem(e1.getCurrentItem())) {
									preferences.replace(e1.getSlot(), replacing);
									display(ItemCategory.QUICK_BUY, player);
								} else if (ShopItem.isNoItem(e1.getCurrentItem())) {

									preferences.replace(e1.getSlot(), replacing);
									display(ItemCategory.QUICK_BUY, player);
								}
								return GUIAction.NONE;
							});
							player.openInventory(replace);
						}
					}
				}

			} else {
				if (ItemCategory.isItemCategory(e.getCurrentItem())) {
					ItemCategory itemCategory = ItemCategory.toItemCategoryEnum(e.getCurrentItem());
					this.display(itemCategory, (Player) e.getWhoClicked());

				} else if (ShopItem.isItemShopItem(e.getCurrentItem())) {
					Player p = (Player) e.getWhoClicked();

					ShopItem item = ShopItem.toItemEnum(e.getCurrentItem());
					if (item == ShopItem.NO_ITEM) {
						return GUIAction.CANCEL_INTERACTION;
					}
					Price.buyItem(item, e.getWhoClicked().getInventory(), p, e.getHotbarButton());
					this.display(category, player);
				}
			}
			return GUIAction.NONE;
		});

		// Quick Buy
		if (category == ItemCategory.QUICK_BUY) {
			Iterator<Integer> slots = InventoryUtils.slotsWith(inventory, (Collection<Material>) null).listIterator();
			preferences.getItems().forEach(items -> {
				if (slots.hasNext()) {
					if (items != null && !items.equals(ShopItem.NO_ITEM)) {
						if (items.isTiered()) {
							ItemStack item = null;
							if (items == ShopItem.WOOD_PICKAXE) {
								if (NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) == items.getTieredItems().size()) {
									item = items.asShopItem(NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player), player, true).clone();
									ItemMeta itemMeta = item.getItemMeta();
									item.setItemMeta(itemMeta);
								} else {
									item = items.asShopItem(NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) + 1, player, true).clone();
								}
							} else if (items == ShopItem.WOOD_AXE) {
								if (NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) == items.getTieredItems().size()) {
									item = items.asShopItem(NovaBedwars.getInstance().getGame().getPlayerAxeTier(player), player, true).clone();
									ItemMeta itemMeta = item.getItemMeta();

									item.setItemMeta(itemMeta);
								} else {
									item = items.asShopItem(NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) + 1, player, true).clone();
								}
							}
							inventory.setItem(slots.next(), item);
						} else {
							if (items.isColored()) {
								inventory.setItem(slots.next(), items.asColoredShopItem(player, true));
							} else {
								inventory.setItem(slots.next(), items.asShopItem(player, true));
							}
						}
					} else {
						inventory.setItem(slots.next(), ShopItem.NO_ITEM.asShopItem(player, false));
					}
				}
			});

			InventoryUtils.slotsWith(inventory, (Collection<Material>) null).forEach(integer -> inventory.setItem(integer, ShopItem.NO_ITEM.asShopItem(player, false)));

			if (NovaBedwars.getInstance().hasHypixelAPI()) {
				ItemBuilder builder = ItemBuilder.getPlayerSkullWithBase64TextureAsBuilder(BedwarsTextures.IMPORT_HYPIXEL_PREFERENCES_BUTTON);
				builder.setName(ChatColor.GOLD + "Import hypixel preferences");
				builder.addLore(ChatColor.AQUA + "This will try to import your bedwars preferences from hypixel");
				builder.setAmount(1);
				if (!BedwarsPreferenceManager.getInstance().isHypixelRequestCooldownActive(player)) {
					inventory.setItem(ItemShop.IMPORT_HYPIXEL_PREFERENCES_SLOT, builder.build());
				} else {
					ItemBuilder cooldown = new ItemBuilder(VersionIndependentUtils.getInstance().getColoredItem(DyeColor.RED, ColoredBlockType.GLASS_PANE)).setName(ChatColor.RED.toString() + ChatColor.BOLD + "Please wait " + BedwarsPreferenceManager.getInstance().getCooldown(player) + " seconds before importing again").setAmount(1);
					inventory.setItem(IMPORT_HYPIXEL_PREFERENCES_SLOT, cooldown.build());
				}

				holder.addClickCallback(IMPORT_HYPIXEL_PREFERENCES_SLOT, (clickedInventory, inventory1, entity, clickedSlot, slotType, clickType) -> {
					player.performCommand("importhypixelpreferences");
					return GUIAction.ALLOW_INTERACTION;
				});
			}

			// Combat
		} else if (category == ItemCategory.COMBAT) {
			ItemStack defaultBow = new ItemBuilder(Material.BOW).setName(ChatColor.GOLD + "Bows").setAmount(1).build();
			ItemStack defaultSword = new ItemBuilder(VersionIndependentMaterial.GOLDEN_SWORD).setName(ChatColor.YELLOW + "Swords").setAmount(1).build();
			ItemStack defaultArmor = new ItemBuilder(Material.LEATHER_CHESTPLATE).setName(ChatColor.WHITE + "Armor").setAmount(1).build();
			inventory.setItem(19, defaultSword);
			inventory.setItem(20, blackbg);

			List<ShopItem> swordList = Arrays.stream(ShopItem.values()).filter(i -> i.asShopItem(player, false).getType().name().contains("SWORD") || i.asShopItem(player, false).getType() == Material.STICK).collect(Collectors.toList());
			List<ShopItem> bowList = Arrays.stream(ShopItem.values()).filter(i -> i.asShopItem(player, false).getType() == Material.BOW || i.asShopItem(player, false).getType() == Material.ARROW).collect(Collectors.toList());
			List<ShopItem> armorList = Arrays.stream(ShopItem.values()).filter(i -> i.asShopItem(player, false).getType().name().contains("BOOTS")).collect(Collectors.toList());

			for (int i = 21; i <= 26; i++) {
				try {
					ItemStack sword = swordList.get(i - 21).asShopItem(player, false);
					Team team = TeamManager.getTeamManager().getPlayerTeam(player.getUniqueId());
					if (NovaBedwars.getInstance().getGame().getBases().stream().filter(baseData -> baseData.getOwner().equals(team)).findFirst().get().hasSharpness()) {
						if (swordList.get(i - 21).isSword()) {
							sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
						}

					}
					inventory.setItem(i, sword);
				} catch (IndexOutOfBoundsException e) {
					inventory.setItem(i, bg);
				}
			}
			inventory.setItem(28, defaultArmor);
			inventory.setItem(29, blackbg);
			for (int i = 30; i <= 34; i++) {
				try {
					ItemStack armor = armorList.get(i - 30).asShopItem(player, false);
					Team team = TeamManager.getTeamManager().getPlayerTeam(player.getUniqueId());
					BaseData data = NovaBedwars.getInstance().getGame().getBases().stream().filter(baseData -> baseData.getOwner().equals(team)).findFirst().get();
					if (!(data.getProtectionLevel() == 0)) {
						armor.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, data.getProtectionLevel());
					}
					inventory.setItem(i, armorList.get(i - 30).asShopItem(player, false));
				} catch (IndexOutOfBoundsException e) {
					inventory.setItem(i, bg);
				}
			}
			for (int i = 39; i <= 43; i++) {
				try {
					inventory.setItem(i, bowList.get(i - 39).asShopItem(player, false));
				} catch (IndexOutOfBoundsException e) {
					inventory.setItem(i, bg);
				}
			}
			inventory.setItem(37, defaultBow);
			inventory.setItem(38, blackbg);

			// Others
		} else {
			Arrays.stream(ShopItem.values()).forEach(items -> {
				if (items.getCategory() == category) {
					if (items.isTiered()) {
						ItemStack item = null;
						if (items == ShopItem.WOOD_PICKAXE) {
							if (NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) == items.getTieredItems().size()) {
								item = items.asShopItem(NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player), player, false).clone();
								ItemMeta itemMeta = item.getItemMeta();
								item.setItemMeta(itemMeta);
							} else {
								item = items.asShopItem(NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) + 1, player, false).clone();
							}
						} else if (items == ShopItem.WOOD_AXE) {

							if (NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) == items.getTieredItems().size()) {
								item = items.asShopItem(NovaBedwars.getInstance().getGame().getPlayerAxeTier(player), player, false).clone();
								ItemMeta itemMeta = item.getItemMeta();
								item.setItemMeta(itemMeta);
							} else {
								item = items.asShopItem(NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) + 1, player, false).clone();
							}
						}
						inventory.addItem(item);
					} else if (items.isColored()) {
						inventory.addItem(items.asColoredShopItem(player, false));
					} else {
						inventory.addItem(items.asShopItem(player, false));
					}
				}
			});
		}
		updateHypixelAPIButton(player);
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