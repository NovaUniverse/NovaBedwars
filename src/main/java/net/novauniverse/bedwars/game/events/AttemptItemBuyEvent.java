package net.novauniverse.bedwars.game.events;

import net.novauniverse.bedwars.game.enums.ShopItem;
import net.novauniverse.bedwars.game.enums.Reason;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AttemptItemBuyEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private final ShopItem item;
	private final Player player;
	private final boolean bought;
	private final Reason reason;

	private boolean disableBuiltInMessage;

	public AttemptItemBuyEvent(ShopItem item, Player player, boolean bought, Reason reason) {
		this.item = item;
		this.player = player;
		this.bought = bought;
		this.reason = reason;

		this.disableBuiltInMessage = false;
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

	public ShopItem getItem() {
		return item;
	}

	public boolean boughtItem() {
		return bought;
	}

	public Reason getReason() {
		return reason;
	}

	public boolean isDisableBuiltInMessage() {
		return disableBuiltInMessage;
	}

	public void setDisableBuiltInMessage(boolean disableBuiltInMessage) {
		this.disableBuiltInMessage = disableBuiltInMessage;
	}
}