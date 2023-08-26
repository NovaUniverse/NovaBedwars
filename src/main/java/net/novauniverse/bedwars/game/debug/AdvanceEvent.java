package net.novauniverse.bedwars.game.debug;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.config.event.BedwarsEvent;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class AdvanceEvent {
    public static final void register() {
        DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
            @Override
            public void onExecute(CommandSender sender, String commandLabel, String[] args) {
                BedwarsEvent event = NovaBedwars.getInstance().getGame().getEvents().stream().filter(bedwarsEvent -> !bedwarsEvent.isFinished()).findFirst().orElse(null);
                if (event != null) {
                    event.finish();
                }
            }

            @Override
            public PermissionDefault getPermissionDefault() {
                return PermissionDefault.OP;
            }

            @Override
            public String getPermission() {
                return "novauniverse.debug.bedwars.advanceevents";
            }

            @Override
            public String getName() {
                return "advanceevents";
            }

            @Override
            public AllowedSenders getAllowedSenders() {
                return AllowedSenders.PLAYERS;
            }
        });
    }
}
