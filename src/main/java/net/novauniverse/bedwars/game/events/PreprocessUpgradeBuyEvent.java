package net.novauniverse.bedwars.game.events;

import net.novauniverse.bedwars.game.enums.Upgrades;
import net.zeeraa.novacore.spigot.teams.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PreprocessUpgradeBuyEvent extends Event  {
	private final HandlerList HANDLERS_LIST = new HandlerList();
	private Upgrades upgrades;
	private Player player;
	private Team team;

	public PreprocessUpgradeBuyEvent(Upgrades item, Player player, Team team) {
		this.upgrades = item;
		this.player = player;
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

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public void setUpgrade(Upgrades upgrades) {
		this.upgrades = upgrades;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public void setTeam(Team team) {
		this.team = team;
	}




}