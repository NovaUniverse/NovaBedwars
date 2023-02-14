package net.novauniverse.bedwars.game.enums;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.object.Price;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.novauniverse.bedwars.utils.TrapCallback;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.abstraction.manager.CustomSpectatorManager;
import net.zeeraa.novacore.spigot.module.modules.cooldown.CooldownManager;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public enum Trap {
	BLINDNESS(new ItemBuilder(Material.COAL).build(), ChatColor.GRAY + "" + ChatColor.BOLD + "Blindness Trap", new TrapCallback<Player, Team>() {
		@Override
		public void execute(Player val, Team val2) {
			val.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 0, false, false));
			CooldownManager.get().set(val, COOLDOWN_ID, 20*20);
			broadcastTrapToTeam(val2, getTrap());
		}
	}),
	COUNTER_OFFENSIVE(new ItemBuilder(VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion()).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build(), ChatColor.YELLOW + "" + ChatColor.BOLD + "Counter-Offensive Trap", new TrapCallback<Player, Team>() {
		@Override
		public void execute(Player val, Team val2) {
			val2.getOnlinePlayers().forEach(player -> {
				if (!CustomSpectatorManager.isSpectator(player)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10*20,0,false, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10*20,0,false, false));
				}
			});
			CooldownManager.get().set(val, COOLDOWN_ID, 20*20);
			broadcastTrapToTeam(val2, getTrap());
		}
	}),
	ALARM(new ItemBuilder(Material.REDSTONE_TORCH_ON).build(),ChatColor.RED + "" + ChatColor.BOLD + "Alarm Trap", new TrapCallback<Player, Team>() {
		@Override
		public void execute(Player val, Team val2) {
			final int[] timeElapsed = {0};
			if (val.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				val.removePotionEffect(PotionEffectType.INVISIBILITY);
			}

			int period = 2;
			BaseData bd = NovaBedwars.getInstance().getGame().getBases().stream().filter(base -> base.getOwner().equals(val2)).findFirst().orElse(null);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (timeElapsed[0] >= (20/period)*10) {
						cancel();
					}
					val.getWorld().playSound(bd.getBedLocation(), Sound.NOTE_PLING, 0.75f, 2);
					timeElapsed[0]++;
				}
			}.runTaskTimer(NovaBedwars.getInstance(), 0, period);
			CooldownManager.get().set(val, COOLDOWN_ID, 20*20);
			broadcastTrapToTeam(val2, getTrap());
		}
	}),
	MINING_FATIGUE(new ItemBuilder(Material.STONE_PICKAXE).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).build(), ChatColor.WHITE + "" + ChatColor.BOLD + "Mining Fatigue Trap", new TrapCallback<Player, Team>() {
		@Override
		public void execute(Player val, Team val2) {
			val.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10*20,1,false,false));
			CooldownManager.get().set(val, COOLDOWN_ID, 20*20);
			broadcastTrapToTeam(val2, getTrap());
		}
	});

	public static final String COOLDOWN_ID = "trapCooldown";

	private ItemStack stack;

	private String name;
	private TrapCallback<Player, Team> callback;
	private static Price defaultPrice = new Price(Material.DIAMOND, 1);
	Trap(ItemStack stack, String name, TrapCallback<Player, Team> callback) {
		ItemBuilder b = new ItemBuilder(stack);
		b.setName(name);
		this.stack = b.build();
		this.name = name;
		callback.setTrap(this);
		this.callback = callback;
	}

	public static Price currentPrice(Team team) {
		BaseData data = NovaBedwars.getInstance().getGame().getBases().stream().filter(bd -> bd.getOwner().equals(team)).findFirst().orElse(null);
		return new Price(defaultPrice.getMaterial(), defaultPrice.getValue() + data.getTraps().size());
	}

	public ItemStack asShopItem(Team team) {

		ItemStack item = stack.clone();
		List<String> lore = new ArrayList<>();
		BaseData data = NovaBedwars.getInstance().getGame().getBases().stream().filter(bd -> bd.getOwner().equals(team)).findFirst().orElse(null);
		if (data.getTraps().size() >= 3) {
			lore.add(ChatColor.RED + "Already have the max amount of traps.");
		} else {
			Price price = new Price(defaultPrice.getMaterial(), defaultPrice.getValue() + data.getTraps().size());
			lore.add(ChatColor.GRAY + "Price: " + ChatColor.WHITE + price.getValue() + " diamond" + (price.getValue() >= 2 ? "s" : ""));
		}
		ItemMeta im = item.getItemMeta();
		im.setLore(lore);
		item.setItemMeta(im);
		return item;
	}

	public static void broadcastTrapToTeam(Team team, Trap trap) {
		team.getOnlinePlayers().forEach(player -> {
			VersionIndependentUtils.get().sendTitle(player, ChatColor.DARK_RED + "" + ChatColor.BOLD + TextUtils.ICON_WARNING + "Trap ACTIVATED. " + TextUtils.ICON_WARNING,
					ChatColor.RED + "Your " + ChatColor.BOLD + ChatColor.stripColor(trap.getName()) + ChatColor.RESET + ChatColor.RED + " has Activated.",
					10,4*20,10);
			player.sendMessage(ChatColor.RED + "Your " + ChatColor.BOLD + ChatColor.stripColor(trap.getName()) + ChatColor.RESET + ChatColor.RED + " has Activated.");
			player.playSound(player.getLocation(), Sound.NOTE_PLING, 1,0.5f);
		});
	}

	public void execute(Player reciever, Team team) {
		callback.execute(reciever, team);
	}

	public TrapCallback<Player, Team> getCallback() {
		return callback;
	}

	public String getName() {
		return name;
	}


	public ItemStack getStack() {
		return stack;
	}
}