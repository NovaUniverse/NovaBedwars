package net.novauniverse.bedwars.game.events;

import net.novauniverse.bedwars.game.enums.Items;
import net.novauniverse.bedwars.game.enums.Reason;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AttemptItemBuyEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private final Items item;
	private final Player player;
	private final boolean bought;
	private final Reason reason;

	public AttemptItemBuyEvent(Items item, Player player, boolean bought, Reason reason) {
		this.item = item;
		this.player = player;
		this.bought = bought;
		this.reason = reason;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

	public Player getPlayer() {
		return player;
	}

	public Items getItem() {
		return item;
	}

	public boolean boughtItem() {
		return bought;
	}
	public Reason getReason() {
		return reason;
	}
}