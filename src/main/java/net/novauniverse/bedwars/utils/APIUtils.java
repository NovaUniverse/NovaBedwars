package net.novauniverse.bedwars.utils;

import net.novauniverse.bedwars.game.modules.BedwarsPreferenceManager;
import net.novauniverse.bedwars.game.modules.PreferenceAPIRequestCallback;
import org.bukkit.entity.Player;

public class APIUtils {
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    public static PreferenceAPIRequestCallback getDefaultRequestCallback(Player player) {
        return (success, exception) -> {
            if (!success) {
                if (exception != null) {
                    System.out.println(ANSI_RED +"Failure> " + ANSI_WHITE + "" + player.getName() + " Fetch retured false. Exception: " + exception.getClass().getName() + " " + exception.getMessage());
                    exception.printStackTrace();
                } else {
                    System.out.println(ANSI_RED + "Failure> " + ANSI_WHITE + "" + player.getName() + " Fetch retured false. No exception");
                }
            } else {
                System.out.println(ANSI_GREEN + "OK> " + ANSI_WHITE + "" + player.getName() + " Import success");

                if (!BedwarsPreferenceManager.getInstance().savePreferences(player, (success1, exception1) -> {
                    if (!success1) {
                        if (exception1 != null) {
                            System.out.println(ANSI_RED + "Failure> " + ANSI_WHITE + "" + player.getName() + " Save retured false. Exception: " + exception1.getClass().getName() + " " + exception1.getMessage());
                            exception1.printStackTrace();
                        } else {
                            System.out.println(ANSI_RED + "Failure> " + ANSI_WHITE +"" + player.getName() + " Save retured false. No exception");
                        }
                    } else {
                        System.out.println(ANSI_GREEN + "OK> " + ANSI_WHITE +"" + player.getName() + " Export success");
                    }
                })) {
                    System.out.println(ANSI_RED + "Failure> " + ANSI_WHITE +"" + player.getName() + " Save method returned false");
                }
            }
        };
    }
    public static void attemptImportHypixelPreferences(Player player) {
        if (!BedwarsPreferenceManager.getInstance().tryImportHypixelPreferences(player, getDefaultRequestCallback(player))) {
            System.out.println(ANSI_RED + "Failure> " + ANSI_WHITE +"" + player.getName() + " Fetch method returned false");
        }
    }
}
