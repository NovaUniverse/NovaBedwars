package net.novauniverse.bedwars.game.debug;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.novauniverse.bedwars.game.commands.ImportBedwarsPreferences;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class CommandFromMessage {
    public static void register() {
        DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
            @Override
            public String getName() {
                return "textcomponenttest";
            }

            @Override
            public String getPermission() {
                return "novauniverse.debug.bedwars.textcomponenttest";
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
                TextComponent starter = new TextComponent(ChatColor.YELLOW + "Click here to import");
                BaseComponent[] hovermessage = new BaseComponent[]{starter};

                TextComponent prefix = new TextComponent(ChatColor.GREEN + "Click ");
                TextComponent here = new TextComponent(ChatColor.GOLD.toString() + ChatColor.BOLD + "here");
                TextComponent command = new TextComponent("/" + ImportBedwarsPreferences.COMMAND_NAME);
                command.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
                command.setBold(true);
                TextComponent suffix = new TextComponent(" or do ");
                suffix.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                TextComponent suffix2 = new TextComponent(" to import your Hypixel preferences.");
                suffix2.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                here.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hovermessage));
                here.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + ImportBedwarsPreferences.COMMAND_NAME));
                ((Player) commandSender).spigot().sendMessage(prefix, here, suffix, command, suffix2);
            }
        });
    }

}
