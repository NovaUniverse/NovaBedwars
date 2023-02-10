package net.novauniverse.bedwars.game.events;


import net.zeeraa.novacore.spigot.abstraction.enums.DeathType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerKilledEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private Player player;
    private Entity killer;
    private DeathType type;

    public PlayerKilledEvent(Player player, Entity killer, DeathType type) {
        this.player = player;
        this.killer = killer;
        this.type = type;
    }

    public Entity getKiller() {
        return killer;
    }

    public DeathType getDeathType() {
        return type;
    }


    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
