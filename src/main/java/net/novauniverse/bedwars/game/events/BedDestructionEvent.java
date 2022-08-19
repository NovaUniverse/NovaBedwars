package net.novauniverse.bedwars.game.events;

import net.zeeraa.novacore.spigot.teams.Team;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedDestructionEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private final Team ownerTeam;
	private final Team attackerTeam;
	private final Player player;

	public BedDestructionEvent(Team ownerTeam, Team attackerTeam, Player player) {
		this.ownerTeam = ownerTeam;
		this.attackerTeam = attackerTeam;
		this.player = player;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

	public Team getOwnerTeam() {
		return ownerTeam;
	}
	
	public Team getAttackerTeam() {
		return attackerTeam;
	}
	
	public Player getPlayer() {
		return player;
	}
}