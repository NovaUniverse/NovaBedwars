package net.novauniverse.bedwars.game;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.config.BedwarsConfig;
import net.novauniverse.bedwars.game.config.ConfiguredBaseData;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class Bedwars extends MapGame implements Listener {
	public static int WEAPON_SLOT_DEFAULT = 0;
	public static int TRACKER_SLOT_DEFAULT = 8;

	private boolean started;
	private boolean ended;

	private BedwarsConfig config;

	private int timeToUpgrade;
	private Task timer;

	private List<Location> allowBreak;

	public List<BaseData> bases;

	public Bedwars() {
		super(NovaBedwars.getInstance());
		timeToUpgrade = 0;
		bases = new ArrayList<>();
		allowBreak = new ArrayList<>();
	}

	public BedwarsConfig getConfig() {
		return config;
	}

	@Override
	public String getName() {
		return "mcf_bedwars";
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
				ConfiguredBaseData b = cfgBase.remove(0);
				BaseData base = b.toBaseData(getWorld(), team);
				bases.add(base);
			} else {
				Log.error("Bedwars", "Not enough configured bases for team " + team.getDisplayName());
			}
		});

		Bukkit.getServer().getOnlinePlayers().stream().filter(p -> players.contains(p.getUniqueId())).forEach(player -> {
			tpToBase(player);
		});
	}

	@Override
	public void onEnd(GameEndReason reason) {
		if (ended) {
			return;
		}

	}

	public void tpToBase(Player player) {
		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		if (team != null) {
			BaseData base = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
			if (base != null) {
				player.setGameMode(GameMode.SURVIVAL);
				PlayerUtils.resetMaxHealth(player);
				player.teleport(base.getBedLocation());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!allowBreak.contains(e.getBlock().getLocation())) {
			allowBreak.add(e.getBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e) {
		if (started) {
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				boolean allow = false;
				// TODO: Some better way to check if its a bed
				if (e.getBlock().getType().name().toLowerCase().contains("bed")) {
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
