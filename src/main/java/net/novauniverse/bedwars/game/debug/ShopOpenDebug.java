package net.novauniverse.bedwars.game.debug;

import net.novauniverse.bedwars.game.enums.ItemCategory;
import net.novauniverse.bedwars.game.shop.ItemShop;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class ShopOpenDebug {
    public static void register() {
        DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
            @Override
            public String getName() {
                return "openshopinv";
            }

            @Override
            public String getPermission() {
                return "novauniverse.debug.bedwars.openshopinv";
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
                new ItemShop().display(ItemCategory.QUICK_BUY, (Player) commandSender);
            }
        });
    }
}
