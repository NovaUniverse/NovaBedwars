package net.novauniverse.bedwars.game.object;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.enums.Items;
import net.novauniverse.bedwars.game.enums.Reason;
import net.novauniverse.bedwars.game.events.AttemptItemBuyEvent;
import net.novauniverse.bedwars.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
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

	public static boolean canBuy(Player player, Items item) {
		if (item.getPrice() == null) {
			return false;
		}
		Price price = item.getPrice();
		if (item == Items.WOOD_AXE) {
			price = item.getItemTier(NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) + 1).getPrice();
		} else if (item == Items.WOOD_PICKAXE) {
			price = item.getItemTier(NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) + 1).getPrice();
		}
		int amountLeft = price.getValue();
		List<Integer> slots = InventoryUtils.slotsWith(player.getInventory(), price.getMaterial());
		for (Integer slot : slots) {
			amountLeft -= player.getInventory().getItem(slot).getAmount();
		}
		return amountLeft <= 0;
	}

	public static void buyItem(Items itemEnum, PlayerInventory inventory, ItemStack item, Player player) {
		boolean bought = true;
		Reason reason = null;
		if (canBuy(player, itemEnum)) {
			if (InventoryUtils.slotsWith(inventory, (Collection<Material>) null).size() == 0) {
				player.sendMessage(ChatColor.RED + "You dont have enough space in your inventory to buy that");
				bought = false;
				reason = Reason.NOT_ENOUGH_SPACE;
			} else if (itemEnum.isArmor()) {
				if (NovaBedwars.getInstance().getGame().getPlayerArmor(player) == itemEnum.getArmorType()) {
					player.sendMessage(ChatColor.RED + "Fail: already have armor"); // Fail silent
					bought = false;
					reason = Reason.ALREADY_HAS_ARMOR;
				} else if (NovaBedwars.getInstance().getGame().getPlayerArmor(player).getTier() > itemEnum.getArmorType().getTier()) {
					player.sendMessage(ChatColor.RED + "Fail: already have better tier"); // Fail silent
					bought = false;
					reason = Reason.ALREADY_HAS_HIGHER_TIER_ARMOR;
				} else {
					NovaBedwars.getInstance().getGame().getAllPlayersArmor().putIfAbsent(player, itemEnum.getArmorType());
					NovaBedwars.getInstance().getGame().getAllPlayersArmor().put(player, itemEnum.getArmorType());
					player.sendMessage(ChatColor.GREEN + "Success: armor bought");
					reason = Reason.ARMOR_BOUGHT;
				}

			} else if (itemEnum.isTiered()) {

				if (itemEnum == Items.WOOD_PICKAXE) {
					for (int i = 0; i < itemEnum.getTieredItems().size(); i++) {
						if (itemEnum.getTieredItems().size() == NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) - 1) {
							player.sendMessage(ChatColor.RED + "You already have the max tier for this item");
							bought = false;
							reason = Reason.ALREADY_HAS_PICKAXE_MAX_TIER;
							break;
						} else {
							if (item.equals(itemEnum.getTieredItems().get(i).asShopItem())) {
								NovaBedwars.getInstance().getGame().getAllPlayersPickaxeTier().put(player, i + 1);
								player.sendMessage(ChatColor.GREEN + "Pickaxe upgraded");
								reason = Reason.PICKAXE_UPGRADE;
							}
						}

					}
				} else if (itemEnum == Items.WOOD_AXE) {
					for (int i = 0; i < itemEnum.getTieredItems().size(); i++) {

						if (itemEnum.getTieredItems().size() == NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) - 1) {
							player.sendMessage(ChatColor.RED + "You already have the max tier for this item");
							bought = false;
							reason = Reason.ALREADY_HAS_AXE_MAX_TIER;
						} else {
							if (item.equals(itemEnum.getTieredItems().get(i).asShopItem())) {
								NovaBedwars.getInstance().getGame().getAllPlayersAxeTier().put(player, i + 1);
								player.sendMessage(ChatColor.GREEN + "Axe upgraded");
								reason = Reason.AXE_UPGRADE;
							}
						}

					}
				}
			} else {
				inventory.addItem(itemEnum.asNormalItem());
				player.sendMessage(ChatColor.GREEN + "Success: normal item bought");
				reason = Reason.NORMAL_ITEM_BOUGHT;

			}
		} else {
			bought = false;
			reason = Reason.NOT_ENOUGHT_MATERIALS;
		}
		if (bought) {
			Price price = itemEnum.getPrice();
			if (itemEnum == Items.WOOD_AXE) {
				price = itemEnum.getItemTier(NovaBedwars.getInstance().getGame().getPlayerAxeTier(player)).getPrice();
			} else if (itemEnum == Items.WOOD_PICKAXE) {
				price = itemEnum.getItemTier(NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player)).getPrice();
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
			NovaBedwars.getInstance().getGame().updatePlayerItems(player);
		}
		Bukkit.getPluginManager().callEvent(new AttemptItemBuyEvent(itemEnum, player, bought, reason));
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