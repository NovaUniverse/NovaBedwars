package net.novauniverse.bedwars.game.object;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.enums.Items;
import net.novauniverse.bedwars.utils.HypixelAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ItemPreferences {
    private final Player player;
    public static String API_KEY = "2e570b70-3120-43e1-b73a-241464828b0e";
    private static ArrayList<Items> arrayList;
    public ItemPreferences(Player player) throws IOException {
        arrayList = new ArrayList<>();
        this.player = player;
        HypixelAPI.bedwarsPreferencesAsList(NovaBedwars.getInstance().getHypixelAPI().getProfile(player)).forEach(s -> {
            if (s.equalsIgnoreCase("null")) {
                arrayList.add(null);
            } else {
                Arrays.stream(Items.values()).forEach(items -> {
                    if (items.getHypixelCounterpart() != null) {
                        if (items.getHypixelCounterpart().equalsIgnoreCase(s)) {
                            arrayList.add(items);
                        }
                    }
                });
            }
        });
        while (arrayList.size() < 21 && arrayList.size() != 21 /* just to make sure*/) {
                arrayList.add(null);
        }
    }
    public Player getPlayer() {
        return player;
    }
    public ArrayList<Items> itemsArray() {
        return arrayList;
    }
    public ArrayList<ItemStack> asItemStackArray() {
        ArrayList<ItemStack> arrayList2 = new ArrayList<>();
        arrayList.forEach(items -> arrayList2.add(items.asShopItem()));
        return arrayList2;
    }
}
