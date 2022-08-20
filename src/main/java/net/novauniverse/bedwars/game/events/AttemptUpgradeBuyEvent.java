package net.novauniverse.bedwars.game.events;

import net.novauniverse.bedwars.game.enums.Upgrades;
import net.zeeraa.novacore.spigot.teams.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AttemptUpgradeBuyEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private Upgrades upgrades;
	private Player player;
	private Team team;
	private boolean success;

	public AttemptUpgradeBuyEvent(Upgrades item, boolean success, Player player, Team team) {
		this.upgrades = item;
		this.player = player;
		this.team = team;
		this.success = success;
	}

	public Upgrades getUpgrade() {
		return upgrades;
	}

	public Player getPlayer() {
		return player;
	}

	public Team getTeam() {
		return team;
	}

	public boolean isSuccess() {
		return success;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

}