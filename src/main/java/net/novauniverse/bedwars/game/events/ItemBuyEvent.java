package net.novauniverse.bedwars.game.events;

import net.novauniverse.bedwars.game.enums.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ItemBuyEvent extends Event  {
	private final HandlerList HANDLERS_LIST = new HandlerList();

	private final Items item;
	private final Player player;


	public ItemBuyEvent(Items item, Player player) {
		this.item = item;
		this.player = player;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public Player getPlayer() {
		return player;
	}


	public Items getItem() {
		return item;
	}




}