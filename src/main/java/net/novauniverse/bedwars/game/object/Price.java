package net.novauniverse.bedwars.game.object;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.enums.ShopItem;
import net.novauniverse.bedwars.game.enums.Reason;
import net.novauniverse.bedwars.game.events.AttemptItemBuyEvent;
import net.novauniverse.bedwars.utils.InventoryUtils;
import net.zeeraa.novacore.commons.utils.Callback;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Price {
	private final Material material;
	private final int price;

	public Price(Material material, int price) {
		this.material = material;
		this.price = price;
	}

	public Material getMaterial() {
		return material;
	}

	public int getValue() {
		return price;
	}

	public static boolean canBuy(Player player, Price price) {
		int amountLeft = price.getValue();
		List<Integer> slots = InventoryUtils.slotsWith(player.getInventory(), price.getMaterial());
		for (Integer slot : slots) {
			amountLeft -= player.getInventory().getItem(slot).getAmount();
		}
		return amountLeft <= 0;
	}

	public static boolean canBuy(Player player, ShopItem item) {
		if (item.getPrice() == null) {
			return false;
		}
		Price price = item.getPrice();
		if (item == ShopItem.WOOD_AXE) {
			price = item.getItemTier(NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) + 1).getPrice();
		} else if (item == ShopItem.WOOD_PICKAXE) {
			price = item.getItemTier(NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) + 1).getPrice();
		}
		int amountLeft = price.getValue();
		List<Integer> slots = InventoryUtils.slotsWith(player.getInventory(), price.getMaterial());
		for (Integer slot : slots) {
			amountLeft -= player.getInventory().getItem(slot).getAmount();
		}
		return amountLeft <= 0;
	}

	public static void buyItem(ShopItem itemEnum, PlayerInventory inventory, ItemStack item, Player player, ClickType ct, int hotbar) {
		boolean bought = true;
		Reason reason;
		Callback callback = null;
		if (canBuy(player, itemEnum)) {
			if (InventoryUtils.slotsWith(inventory, (Collection<Material>) null).size() == 0) {
				player.sendMessage(ChatColor.RED + "You dont have enough space in your inventory to buy that");
				bought = false;
				reason = Reason.NOT_ENOUGH_SPACE;
			} else if (itemEnum.isArmor()) {
				if (NovaBedwars.getInstance().getGame().getPlayerArmor(player) == itemEnum.getArmorType()) {
					player.sendMessage(ChatColor.RED + "You already have this armor.");
					bought = false;
					reason = Reason.ALREADY_HAS_ARMOR;
				} else if (NovaBedwars.getInstance().getGame().getPlayerArmor(player).getTier() > itemEnum.getArmorType().getTier()) {
					player.sendMessage(ChatColor.RED + "You already have a better tier of armor.");
					bought = false;
					reason = Reason.ALREADY_HAS_HIGHER_TIER_ARMOR;
				} else {
					callback = () -> {
						NovaBedwars.getInstance().getGame().getAllPlayersArmor().putIfAbsent(player, itemEnum.getArmorType());
						NovaBedwars.getInstance().getGame().getAllPlayersArmor().put(player, itemEnum.getArmorType());
					};
					reason = Reason.ARMOR_BOUGHT;
				}

			} else if (itemEnum.isTiered()) {
				if (itemEnum == ShopItem.WOOD_PICKAXE) {
					for (int i = 0; i < itemEnum.getTieredItems().size(); i++) {
						if (itemEnum.getTieredItems().size() == NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) - 1) {
							player.sendMessage(ChatColor.RED + "You already have the max tier for the Pickaxe.");
							bought = false;
							reason = Reason.ALREADY_HAS_PICKAXE_MAX_TIER;
							break;
						} else {
							if (item.equals(itemEnum.getTieredItems().get(i).getShopItem())) {
								int finalI = i;
								callback = () -> NovaBedwars.getInstance().getGame().getAllPlayersPickaxeTier().put(player, finalI + 1);
								reason = Reason.PICKAXE_UPGRADE;
							}
						}

					}
				} else if (itemEnum == ShopItem.WOOD_AXE) {
					for (int i = 0; i < itemEnum.getTieredItems().size(); i++) {
						if (itemEnum.getTieredItems().size() == NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) - 1) {
							player.sendMessage(ChatColor.RED + "You already have the max tier for the Axe.");
							bought = false;
							reason = Reason.ALREADY_HAS_AXE_MAX_TIER;
							break;
						} else {
							if (item.equals(itemEnum.getTieredItems().get(i).getShopItem())) {
								int finalI = i;
								callback = () -> NovaBedwars.getInstance().getGame().getAllPlayersAxeTier().put(player, finalI + 1);

								reason = Reason.AXE_UPGRADE;
							}
						}

					}
				}
			} else if (itemEnum.isSword()) {
				if (InventoryUtils.slotsWith(player.getInventory(), Material.WOOD_SWORD).isEmpty()) {
					callback = () -> {
						if (ct == ClickType.NUMBER_KEY) {
							ItemStack itemToAdd = inventory.getItem(hotbar);
							inventory.setItem(hotbar, itemEnum.asNormalItem());
							if (itemToAdd != null && itemToAdd.getType() != Material.AIR) {
								inventory.addItem(itemToAdd);
							}
						} else {
							inventory.addItem(itemEnum.asNormalItem());
						}
					};
				} else {
					callback = () -> {
						List<Integer> values = InventoryUtils.slotsWith(player.getInventory(), Material.WOOD_SWORD);
						Collections.sort(values);
						int toReplace = values.get(0);
						if (ct == ClickType.NUMBER_KEY) {
							inventory.setItem(toReplace, new ItemStack(Material.AIR));
							ItemStack itemToAdd = inventory.getItem(hotbar);
							inventory.setItem(hotbar, itemEnum.asNormalItem());
							if (itemToAdd != null && itemToAdd.getType() != Material.AIR) {
								inventory.addItem(itemToAdd);
							}
						} else {
							inventory.setItem(toReplace, itemEnum.asNormalItem());
						}
					};
				}

			} else if (itemEnum.isColored()) {
				callback = () -> {
					if (ct == ClickType.NUMBER_KEY) {
						ItemStack itemToAdd = inventory.getItem(hotbar);
						inventory.setItem(hotbar, itemEnum.asColoredNormalItem(player));
						if (itemToAdd != null && itemToAdd.getType() != Material.AIR) {
							inventory.addItem(itemToAdd);
						}
					} else {
						inventory.addItem(itemEnum.asColoredNormalItem(player));
					}
				};

			} else {
				callback = () -> {
					if (ct == ClickType.NUMBER_KEY) {
						ItemStack itemToAdd = inventory.getItem(hotbar);
						inventory.setItem(hotbar, itemEnum.asNormalItem());
						if (itemToAdd != null && itemToAdd.getType() != Material.AIR) {
							inventory.addItem(itemToAdd);
						}
					} else {
						inventory.addItem(itemEnum.asNormalItem());
					}
				};

			}
				reason = Reason.NORMAL_ITEM_BOUGHT;
		} else {
			bought = false;
			reason = Reason.NOT_ENOUGHT_MATERIALS;
		}
		if (bought) {
			Price price = itemEnum.getPrice();
			ItemStack tieredItemBuy = new ItemStack(Material.AIR);
			if (itemEnum == ShopItem.WOOD_AXE) {
				price = itemEnum.getItemTier(NovaBedwars.getInstance().getGame().getPlayerAxeTier(player)).getPrice();
				tieredItemBuy = itemEnum.getItemTier(NovaBedwars.getInstance().getGame().getPlayerAxeTier(player)).getItemStack();
			} else if (itemEnum == ShopItem.WOOD_PICKAXE) {
				price = itemEnum.getItemTier(NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player)).getPrice();
				tieredItemBuy = itemEnum.getItemTier(NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player)).getItemStack();
			}
			int amountLeft = price.getValue();

			List<Integer> slots = InventoryUtils.slotsWith(player.getInventory(), price.getMaterial());
			for (Integer slot : slots) {
				amountLeft -= player.getInventory().getItem(slot).getAmount();
				if (amountLeft <= 0) {
					if (amountLeft == 0) {
						player.getInventory().setItem(slot, new ItemStack(Material.AIR, 0));
					} else {
						player.getInventory().getItem(slot).setAmount(amountLeft * -1);
					}
					break;
				} else {
					player.getInventory().setItem(slot, new ItemStack(Material.AIR, 0));
				}
			}
			if (callback != null) {
				callback.execute();
			}
			NovaBedwars.getInstance().getGame().updatePlayerItems(player);
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.75f,2);
			if (itemEnum.isTiered()) {
				player.sendMessage(ChatColor.GREEN + "Bought " + ChatColor.BOLD + (tieredItemBuy.getItemMeta().getDisplayName() != null ? tieredItemBuy.getItemMeta().getDisplayName() : getMaterialName(tieredItemBuy.getType())));
			} else if (itemEnum.isArmor()) {
				player.sendMessage(ChatColor.GREEN + "Bought " + ChatColor.BOLD + itemEnum.getArmorType().getShopName());
			} else {
				player.sendMessage(ChatColor.GREEN + "Bought " + ChatColor.BOLD + (itemEnum.asNormalItem().getItemMeta().getDisplayName() != null ? itemEnum.asNormalItem().getItemMeta().getDisplayName() : getMaterialName(itemEnum.asNormalItem().getType())));
			}

		}
		Bukkit.getPluginManager().callEvent(new AttemptItemBuyEvent(itemEnum, player, bought, reason));
	}


	private static String getMaterialName(Material material) {
		String s = material.name().toLowerCase().replace("_", " ");
		String build = "";
		for (char c : s.toCharArray()) {
			if (build.length() == 0) {
				build += (c + "").toUpperCase();
			} else {
				if (build.charAt(build.length()-1) == ' ') {
					build += (c + "").toUpperCase();
				} else {
					build += c;
				}
			}
		}
		return build;
	}

	public static void buyUpgrade(Player player, Price price) {
		int amountLeft = price.getValue();
		List<Integer> slots = InventoryUtils.slotsWith(player.getInventory(), price.getMaterial());
		for (Integer slot : slots) {
			amountLeft -= player.getInventory().getItem(slot).getAmount();
			if (amountLeft <= 0) {
				if (amountLeft == 0) {
					player.getInventory().setItem(slot, new ItemStack(Material.AIR, 0));
				} else {
					player.getInventory().getItem(slot).setAmount(amountLeft * -1);
				}
				break;
			} else {
				player.getInventory().setItem(slot, new ItemStack(Material.AIR, 0));
			}
		}
	}
}