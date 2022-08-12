package net.novauniverse.bedwars.game.object;

import net.novauniverse.bedwars.game.enums.Items;
import net.novauniverse.bedwars.utils.HypixelAPI;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemPreferences extends ArrayList<Items> {
    private Player player;
    public static String API_KEY = "2e570b70-3120-43e1-b73a-241464828b0e";
    public ItemPreferences(Player player) throws IOException {
        this.player = player;
        HypixelAPI.bedwarsPreferencesAsList(new HypixelAPI(API_KEY).getProfile(player)).forEach(s -> {
            if (s.equalsIgnoreCase("null")) {
                this.add(null);
            } else {
                Arrays.stream(Items.values()).forEach(items -> {
                    if (items.getHypixelCounterpart().equalsIgnoreCase(s)) {
                        this.add(items);
                    }
                });
            }
        });
        while (this.size() < 21 && this.size() != 21 /* just to make sure*/) {
                this.add(null);
        }
    }
    public Player getPlayer() {
        return player;
    }
}
