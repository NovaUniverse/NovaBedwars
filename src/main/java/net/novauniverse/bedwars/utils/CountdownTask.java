package net.novauniverse.bedwars.utils;

import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import org.bukkit.entity.Player;

public class CountdownTask extends SimpleTask {
    private Player player;

    public CountdownTask(Runnable runnable, long period, Player player) {
        super(runnable, period);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
