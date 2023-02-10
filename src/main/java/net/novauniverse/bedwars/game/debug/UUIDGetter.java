package net.novauniverse.bedwars.game.debug;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class UUIDGetter {
    public static void register() {
        DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
            @Override
            public String getName() {
                return "uuidgetter";
            }

            @Override
            public String getPermission() {
                return "novauniverse.debug.bedwars.uuidgetter";
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
                Player player = (Player) commandSender;
                TextComponent configstarter = new TextComponent(player.getUniqueId().toString());
                BaseComponent[] config = new BaseComponent[]{configstarter};
                TextComponent playeruuid = new TextComponent(player.getUniqueId().toString());
                playeruuid.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, config));
                playeruuid.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, playeruuid.getText()));
               ((Player) commandSender).spigot().sendMessage(playeruuid);
            }
        });
    }
}
