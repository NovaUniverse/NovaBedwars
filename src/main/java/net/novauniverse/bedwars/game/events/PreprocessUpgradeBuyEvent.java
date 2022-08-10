package net.novauniverse.bedwars.game.events;

import net.novauniverse.bedwars.game.enums.Upgrades;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PreprocessUpgradeBuyEvent extends Event implements Cancellable {
	private final HandlerList HANDLERS_LIST = new HandlerList();
	private Result buy = Result.DEFAULT;
	private Upgrades upgrades;
	private Player player;
	private InventoryClickEvent clickEvent;

	public PreprocessUpgradeBuyEvent(Upgrades item, Player player, InventoryClickEvent e) {
		this.upgrades = item;
		this.player = player;
		this.clickEvent = e;
	}

	public Upgrades getUpgrade() {
		return upgrades;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public Result buy() {
		return buy;
	}

	public void setBuy(Result buy) {
		this.buy = buy;
	}

	@Override
	public boolean isCancelled() {
		return this.buy() == Result.DENY;
	}

	public InventoryClickEvent getEvent() {
		return clickEvent;
	}

	@Override
	public void setCancelled(boolean b) {
		this.setBuy(b ? Result.DENY : (this.buy() == Result.DENY ? Result.DEFAULT : this.buy()));
		clickEvent.setCancelled(b);
	}
}