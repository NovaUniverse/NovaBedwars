package net.novauniverse.bedwars.game.debug;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.bedwars.game.modules.BedwarsPreferenceManager;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;

public class MissileWarsDebugCommands {
	public static final void register() {
		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public void onExecute(CommandSender sender, String commandLabel, String[] args) {
				Player player = (Player) sender;
				if (!BedwarsPreferenceManager.getInstance().tryImportHypixelPreferences(player, (success, exception) -> {
					if (!success) {
						if (exception != null) {
							sender.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Fetch retured false. Exception: " + exception.getClass().getName() + " " + exception.getMessage());
							exception.printStackTrace();
						} else {
							sender.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Fetch retured false. No exception");
						}
					} else {
						sender.sendMessage(ChatColor.GREEN + "OK>" + ChatColor.WHITE + " Import success");

						if (!BedwarsPreferenceManager.getInstance().savePreferences(player, (success1, exception1) -> {
							if (!success1) {
								if (exception1 != null) {
									sender.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Save retured false. Exception: " + exception1.getClass().getName() + " " + exception1.getMessage());
									exception1.printStackTrace();
								} else {
									sender.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Save retured false. No exception");
								}
							} else {
								sender.sendMessage(ChatColor.GREEN + "OK>" + ChatColor.WHITE + " Export success");
							}
						})) {
							sender.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Save method returned false");
						}
					}
				})) {
					sender.sendMessage(ChatColor.DARK_RED + "Failure>" + ChatColor.WHITE + " Fetch method returned false");
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