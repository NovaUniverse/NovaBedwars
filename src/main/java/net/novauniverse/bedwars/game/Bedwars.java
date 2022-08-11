package net.novauniverse.bedwars.game;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.config.BedwarsConfig;
import net.novauniverse.bedwars.game.config.ConfiguredBaseData;
import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.entity.NPCType;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;
import net.zeeraa.novacore.spigot.utils.RandomFireworkEffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class Bedwars extends MapGame implements Listener {
	public static int WEAPON_SLOT_DEFAULT = 0;
	public static int TRACKER_SLOT_DEFAULT = 8;

	private boolean started;
	private boolean ended;

	private BedwarsConfig config;

	private int beginTimer;
	private boolean beginTimerFinished;
	private Task beginTask;

	private List<Location> allowBreak;
	private List<BedwarsNPC> npcs;
	private List<BaseData> bases;

	public Bedwars() {
		super(NovaBedwars.getInstance());
		bases = new ArrayList<>();
		allowBreak = new ArrayList<>();
		npcs = new ArrayList<>();

		beginTimer = 10;
		beginTimerFinished = false;

		beginTask = new SimpleTask(this.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if (beginTimer > 0) {
					VersionIndependentSound.NOTE_PLING.broadcast();
					Bukkit.getServer().getOnlinePlayers().forEach(player -> VersionIndependentUtils.get().sendTitle(player, "", ChatColor.GREEN + "Starting in " + ChatColor.AQUA + beginTimer, 0, 21, 0));
					beginTimer--;
				} else {
					VersionIndependentSound.NOTE_PLING.broadcast(1.0F, 1.5F);
					Bukkit.getServer().getOnlinePlayers().forEach(player -> VersionIndependentUtils.get().sendTitle(player, "", ChatColor.GREEN + "GO", 0, 20, 20));
					beginTimerFinished = true;
					Task.tryStopTask(beginTask);
					sendBeginEvent();
				}
			}
		}, 20L);
	}

	public BedwarsConfig getConfig() {
		return config;
	}

	@Override
	public String getName() {
		return "nova_bedwars";
	}

	@Override
	public String getDisplayName() {
		return "Bedwars";
	}

	@Override
	public PlayerQuitEliminationAction getPlayerQuitEliminationAction() {
		return PlayerQuitEliminationAction.DELAYED;
	}

	@Override
	public boolean eliminatePlayerOnDeath(Player player) {
		return false;
	}

	@Override
	public boolean isPVPEnabled() {
		return true;
	}

	@Override
	public boolean autoEndGame() {
		return false;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

	@Override
	public boolean hasEnded() {
		return ended;
	}

	@Override
	public boolean isFriendlyFireAllowed() {
		return false;
	}

	@Override
	public boolean canAttack(LivingEntity attacker, LivingEntity target) {
		return false;
	}

	@Override
	public void onStart() {
		if (started) {
			return;
		}

		BedwarsConfig config = (BedwarsConfig) getActiveMap().getMapData().getMapModule(BedwarsConfig.class);
		if (config == null) {
			Log.fatal("NovaBedwars", "Map " + this.getActiveMap().getMapData().getMapName() + " has no config");
			Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Bedwars has gone into an error and has to be stopped");
			endGame(GameEndReason.ERROR);
			return;
		}
		this.config = config;

		List<ConfiguredBaseData> cfgBase = new ArrayList<>(config.getBases());
		TeamManager.getTeamManager().getTeams().forEach(team -> {
			if (cfgBase.size() > 0) {
				ConfiguredBaseData configuredBase = cfgBase.remove(0);
				BaseData base = configuredBase.toBaseData(getWorld(), team);
				bases.add(base);
				
				BedwarsNPC itemShopNPC = new BedwarsNPC(base.getItemShopLocation(), NPCType.ITEMS);
				BedwarsNPC upgradesShopNPC = new BedwarsNPC(base.getItemShopLocation(), NPCType.UPGRADES);
				
				itemShopNPC.spawn();
				upgradesShopNPC.spawn();
				
				npcs.add(itemShopNPC);
				npcs.add(upgradesShopNPC);
			} else {
				Log.error("Bedwars", "Not enough configured bases for team " + team.getDisplayName());
			}
		});

		Bukkit.getServer().getOnlinePlayers().forEach(p -> tpToSpectator(p));

		Bukkit.getServer().getOnlinePlayers().stream().filter(p -> players.contains(p.getUniqueId())).forEach(player -> {
			tpToBase(player);
		});

		Task.tryStartTask(beginTask);

		started = true;
	}

	@Override
	public void tpToSpectator(Player player) {
		PlayerUtils.clearPlayerInventory(player);
		PlayerUtils.resetMaxHealth(player);
		PlayerUtils.resetPlayerXP(player);
		player.setGameMode(GameMode.SPECTATOR);
		player.teleport(getActiveMap().getSpectatorLocation());
	}

	@Override
	public void onEnd(GameEndReason reason) {
		if (ended || !started) {
			return;
		}

		getActiveMap().getStarterLocations().forEach(location -> {
			Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
			FireworkMeta fwm = fw.getFireworkMeta();

			fwm.setPower(2);
			fwm.addEffect(RandomFireworkEffect.randomFireworkEffect());

			if (random.nextBoolean()) {
				fwm.addEffect(RandomFireworkEffect.randomFireworkEffect());
			}

			fw.setFireworkMeta(fwm);
		});

		Bukkit.getServer().getOnlinePlayers().forEach(player -> {
			VersionIndependentUtils.get().resetEntityMaxHealth(player);
			player.setFoodLevel(20);
			PlayerUtils.clearPlayerInventory(player);
			PlayerUtils.resetPlayerXP(player);
			player.setGameMode(GameMode.SPECTATOR);
			if (!NovaBedwars.getInstance().isDisableDefaultEndSound()) {
				VersionIndependentUtils.get().playSound(player, player.getLocation(), VersionIndependentSound.WITHER_DEATH, 1F, 1F);
			}
		});

		Task.tryStopTask(beginTask);

		allowBreak.clear();

		ended = true;
	}

	public void tpToBase(Player player) {
		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		if (team != null) {
			BaseData base = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
			if (base != null) {
				player.setGameMode(GameMode.SURVIVAL);
				player.setHealth(player.getMaxHealth());
				player.setSaturation(20);
				player.setFoodLevel(20);
				PlayerUtils.clearPotionEffects(player);
				player.teleport(base.getBedLocation());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.SPECTATOR) {
			if (started && !ended && !beginTimerFinished) {
				Location to = e.getFrom().clone();

				to.setY(e.getTo().getY());
				to.setYaw(e.getTo().getYaw());
				to.setPitch(e.getTo().getPitch());

				e.setTo(to);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (started) {
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				if (!beginTimerFinished) {
					e.setCancelled(true);
				}
			}

			if (!allowBreak.contains(e.getBlock().getLocation())) {
				allowBreak.add(e.getBlock().getLocation());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
		npcs.stream().filter(n -> n.getVillager().getUniqueId().equals(e.getRightClicked().getUniqueId())).findFirst().ifPresent(clickedNPC -> {
			switch (clickedNPC.getType()) {
			case ITEMS:
			// TODO: Open item shop ui
				break;
				
			case UPGRADES:
				// TODO: Open upgrades shop ui
				break;

			default:
				break;
			}
		});
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e) {
		if (started) {
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				if (!beginTimerFinished) {
					e.setCancelled(true);
				}

				boolean allow = false;
				if (VersionIndependentUtils.get().isBed(e.getBlock())) {
					allow = true;
				} else if (allowBreak.contains(e.getBlock().getLocation())) {
					allow = true;
				}

				if (!allow) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "You can only break blocks placed by players");
				}
			}
		}
	}
}
