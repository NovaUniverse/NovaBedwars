package net.novauniverse.bedwars.game.events;

import net.novauniverse.bedwars.game.enums.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PreprocessItemBuyEvent extends Event implements Cancellable {
	private final HandlerList HANDLERS_LIST = new HandlerList();
	private Result buy = Result.DEFAULT;

	private final Items item;
	private final Player player;
	private final int slot;
	private final InventoryClickEvent clickEvent;

	public PreprocessItemBuyEvent(Items item, Player player, int slot, InventoryClickEvent e) {
		this.item = item;
		this.player = player;
		this.slot = slot;
		this.clickEvent = e;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public Player getPlayer() {
		return player;
	}

	public int getSlot() {
		return slot;
	}

	public Items getItem() {
		return item;
	}

	@Override
	public boolean isCancelled() {
		return this.buy() == Result.DENY;
	}

	public Result buy() {
		return buy;
	}

	public void setBuy(Result buy) {
		this.buy = buy;
	}

	public InventoryClickEvent getEvent() {
		return clickEvent;
	}

	@Override
	public void setCancelled(boolean b) {
		this.setBuy(b ? Result.DENY : (this.buy() == Result.DENY ? Result.DEFAULT : this.buy()));
		clickEvent.setCancelled(true);
	}
}