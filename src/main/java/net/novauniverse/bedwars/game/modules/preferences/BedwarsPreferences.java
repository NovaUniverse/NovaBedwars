package net.novauniverse.bedwars.game.modules.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;

import net.novauniverse.bedwars.game.enums.ShopItem;
import net.zeeraa.novacore.commons.log.Log;

public class BedwarsPreferences {
	private final UUID uuid;
	private List<ShopItem> items;

	public BedwarsPreferences(UUID uuid, List<ShopItem> items) {
		this.uuid = uuid;
		this.items = items;
	}

	public UUID getUuid() {
		return uuid;
	}

	public List<ShopItem> getItems() {
		return items;
	}

	public void setItems(List<ShopItem> items) {
		this.items = items;
	}

	public JSONArray toJSON() {
		JSONArray json = new JSONArray();
		items.forEach(i -> json.put(i == null ? "null" : i.name()));
		return json;
	}

	public static List<ShopItem> parseItems(JSONArray jsonArray) {
		List<ShopItem> items = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			String itemName = jsonArray.getString(i);
			if (itemName == null) {
				items.add(null);
			} else {
				try {
					ShopItem item = itemName.equals("null") ? ShopItem.NO_ITEM : ShopItem.valueOf(itemName);
					items.add(item);
				} catch (IllegalArgumentException e) {
					Log.warn("BedwarsPreferences", "Unknown item " + itemName);
				}
			}
		}

		return items;
	}

	public static BedwarsPreferences fromJSON(UUID uuid, JSONArray jsonArray) {
		List<ShopItem> items = BedwarsPreferences.parseItems(jsonArray);
		return new BedwarsPreferences(uuid, items);
	}

	public static BedwarsPreferences empty(UUID uuid) {
		return new BedwarsPreferences(uuid, new ArrayList<>());
	}

	public static BedwarsPreferences defaultPreferences(UUID uuid) {
		List<ShopItem> defaults = new ArrayList<>();

		defaults.add(ShopItem.WOOL);
		defaults.add(ShopItem.STONE_SWORD);
		defaults.add(ShopItem.CHAINMAIL_ARMOR);
		defaults.add(ShopItem.NO_ITEM);
		defaults.add(ShopItem.BOW);
		defaults.add(ShopItem.SPEED);
		defaults.add(ShopItem.TNT);
		defaults.add(ShopItem.ENDER_PEARL);
		defaults.add(ShopItem.WOOD);
		defaults.add(ShopItem.IRON_SWORD);
		defaults.add(ShopItem.IRON_ARMOR);
		defaults.add(ShopItem.SHEARS);
		defaults.add(ShopItem.ARROW);
		defaults.add(ShopItem.INVISIBLE);
		defaults.add(ShopItem.WATER_BUCKET);

		return new BedwarsPreferences(uuid, defaults);
	}
}