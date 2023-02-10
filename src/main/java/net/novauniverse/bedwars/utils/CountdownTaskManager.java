package net.novauniverse.bedwars.utils;


import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CountdownTaskManager {

    private static List<CountdownTask> tasks = new ArrayList<>();

    public static void addTask(CountdownTask task) {
        task.start();
        tasks.add(task);
    }
    public static List<CountdownTask> getTasks() {
        return tasks;
    }
    public static CountdownTask getTask(Player player) {
       return tasks.stream().filter(countdownTask -> countdownTask.getPlayer().equals(player)).findFirst().orElse(null);
    }
    public static void remove(CountdownTask task) {
        task.stopIfRunning();
        tasks.remove(task);
    }
    public static void remove(Player player) {
        getTask(player).stopIfRunning();
        tasks.remove(getTask(player));
    }
}
