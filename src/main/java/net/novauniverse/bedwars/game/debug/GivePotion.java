package net.novauniverse.bedwars.game.debug;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.utils.PotionItemBuilder;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;
import net.zeeraa.novacore.spigot.version.v1_8_R3.VersionIndependentUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class GivePotion {
    public static final void register() {
        DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
            @Override
            public String getName() {
                return "givepotion";
            }

            @Override
            public String getPermission() {
                return "novauniverse.debug.bedwars.givepotion";
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

                PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, 20 * 4, 1);
                ItemStack stack = new PotionItemBuilder(Material.POTION).setPotionEffect(effect, true).build();
                // PotionMeta meta = (PotionMeta) pot.getItemMeta();
                // PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, 1, 20* 20);
                // meta.addCustomEffect(effect, false);
                // meta.setMainEffect(PotionEffectType.SPEED);
                // pot.setItemMeta(meta);
                ((Player) commandSender).getInventory().addItem(stack);
                commandSender.sendMessage(stack.toString());
                commandSender.sendMessage(stack.getDurability() + "");
            }
        });
    }
}
