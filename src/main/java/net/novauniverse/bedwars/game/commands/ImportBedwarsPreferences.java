package net.novauniverse.bedwars.game.commands;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.modules.preferences.BedwarsPreferenceManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

public class ImportBedwarsPreferences extends NovaCommand {
    public static final String COMMAND_NAME = "importhypixelpreferences";

    public ImportBedwarsPreferences() {
        super(COMMAND_NAME, NovaBedwars.getInstance());
        setAllowedSenders(AllowedSenders.PLAYERS);
        setDescription("Import bedwars preferences");
        setPermission("bedwars.commands.import");
        setPermissionDefaultValue(PermissionDefault.TRUE);
        setEmptyTabMode(true);
        setFilterAutocomplete(true);
        addHelpSubCommand();
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (BedwarsPreferenceManager.getInstance().isHypixelRequestCooldownActive(player)) {

            player.sendMessage(ChatColor.RED + "Please wait " + BedwarsPreferenceManager.getInstance().getCooldown(player) + " seconds before trying to import preferences again");
        } else {
            if (!BedwarsPreferenceManager.getInstance().tryImportHypixelPreferences(player, (success, exception) -> {
                if (success) {
                    player.closeInventory();
                    VersionIndependentSound.NOTE_PLING.play(player);
                    player.sendMessage(ChatColor.GREEN + "Hypixel preferences imported");
                } else {
                    if (exception == null) {
                        player.closeInventory();
                        VersionIndependentSound.NOTE_PLING.play(player);
                        player.sendMessage(ChatColor.RED + "Could not import your hypixel preferences. this could be caused by you changing your minecraft name recently or an error on our side");
                    } else {
                        player.closeInventory();
                        player.sendMessage(ChatColor.DARK_RED + "An error occured while trying to import your preferences. " + exception.getClass().getName());
                        Log.error("ItemShop", "Failed to import preferences for player " + player.getName() + ". " + exception.getClass().getName() + " " + exception.getMessage());
                        exception.printStackTrace();
                    }
                }
            })) {
                player.sendMessage(ChatColor.DARK_RED + "Import failure");
            }
        }
        return true;
    }
}
