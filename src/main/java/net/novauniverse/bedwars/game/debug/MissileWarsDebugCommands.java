package net.novauniverse.bedwars.game.debug;

import net.novauniverse.bedwars.game.modules.preferences.BedwarsPreferenceManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;

public class MissileWarsDebugCommands {
	public static final void register() {
		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public void onExecute(CommandSender sender, String commandLabel, String[] args) {
				Player player = (Player) sender;
				if(BedwarsPreferenceManager.getInstance().tryImportHypixelPreferences(player, (success, exception) -> {
					if(success) {
						sender.sendMessage(ChatColor.GREEN + "Preferences imported!");
					} else {
						if(exception == null) {
							sender.sendMessage(ChatColor.RED + "Failed to import");
						} else {
							sender.sendMessage(ChatColor.RED + "Failed to import. " + exception.getClass().getName() + " " + exception.getMessage());
							exception.printStackTrace();
						}
					}
				})) {
					sender.sendMessage(ChatColor.BLUE + "Import started...");
				} else {
					sender.sendMessage(ChatColor.RED + "Hypixel API not enabled");
				}
			}

			@Override
			public PermissionDefault getPermissionDefault() {
				return PermissionDefault.OP;
			}

			@Override
			public String getPermission() {
				return "novauniverse.debug.bedwars.savehypixelpreferences";
			}

			@Override
			public String getName() {
				return "savehypixelpreferences";
			}

			@Override
			public AllowedSenders getAllowedSenders() {
				return AllowedSenders.PLAYERS;
			}
		});
	}
}