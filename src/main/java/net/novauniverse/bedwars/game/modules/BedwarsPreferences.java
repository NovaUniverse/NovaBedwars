package net.novauniverse.bedwars.game.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;

import net.novauniverse.bedwars.game.enums.Items;
import net.zeeraa.novacore.commons.log.Log;

public class BedwarsPreferences {
	private final UUID uuid;
	private List<Items> items;

	public BedwarsPreferences(UUID uuid, List<Items> items) {
		this.uuid = uuid;
		this.items = items;
	}

	public UUID getUuid() {
		return uuid;
	}

	public List<Items> getItems() {
		return items;
	}

	public void setItems(List<Items> items) {
		this.items = items;
	}

	public JSONArray toJSON() {
		JSONArray json = new JSONArray();
		items.forEach(i -> json.put(i == null ? "null" : i.name()));
		return json;
	}

	public static List<Items> parseItems(JSONArray jsonArray) {
		List<Items> items = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			String itemName = jsonArray.getString(i);
			if (itemName == null) {
				items.add(null);
			} else {
				try {
					Items item = Items.valueOf(itemName);
					items.add(item);
				} catch (IllegalArgumentException e) {
					Log.warn("BedwarsPreferences", "Unknown item " + itemName);
				}
			}
		}

		return items;
	}

	public static BedwarsPreferences fromJSON(UUID uuid, JSONArray jsonArray) {
		List<Items> items = BedwarsPreferences.parseItems(jsonArray);
		return new BedwarsPreferences(uuid, items);
	}

	public static BedwarsPreferences empty(UUID uuid) {
		return new BedwarsPreferences(uuid, new ArrayList<>());
	}
}