package net.novauniverse.bedwars.game.debug;

import net.novauniverse.bedwars.game.enums.Items;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.Arrays;

public class ShopItemMetasDebugger {
    public static void register() {
        DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
            @Override
            public String getName() {
                return "itemmetachecker";
            }

            @Override
            public String getPermission() {
                return "novauniverse.debug.bedwars.itemmetachecker";
            }

            @Override
            public AllowedSenders getAllowedSenders() {
                return AllowedSenders.PLAYERS;
            }

            @Override
            public PermissionDefault getPermissionDefault() {
                return PermissionDefault.OP;
            }

            @Override
            public void onExecute(CommandSender commandSender, String s, String[] strings) {
                Arrays.stream(Items.values()).forEach(items -> commandSender.sendMessage("ITEM META: " + (items.asShopItem().getItemMeta())));
                commandSender.sendMessage("------------------------------");
                commandSender.sendMessage("");
                commandSender.sendMessage("------------------------------");
                Arrays.stream(Items.values()).forEach(items -> commandSender.sendMessage("ITEM: " + (items.asShopItem())));
            }
        });
    }
}
