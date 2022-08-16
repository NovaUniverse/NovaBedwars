package net.novauniverse.bedwars.game.debug;

import net.novauniverse.bedwars.NovaBedwars;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class HashMapDebugger {
    public static final void register() {
        DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
            @Override
            public String getName() {
                return "hashmapchecker";
            }

            @Override
            public String getPermission() {
                return "novauniverse.debug.bedwars.hashmapchecker";
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
                NovaBedwars.getInstance().getAllPlayerArmor().forEach((player, armortype) -> commandSender.sendMessage(player.getName() + " : " + armortype));
                NovaBedwars.getInstance().getAllPlayersPickaxeTier().forEach((player, integer) -> commandSender.sendMessage(player.getName() + " : " + integer));
                NovaBedwars.getInstance().getAllPlayersAxeTier().forEach((player, integer) -> commandSender.sendMessage(player.getName() + " : " + integer));
            }
        });
    }
}
