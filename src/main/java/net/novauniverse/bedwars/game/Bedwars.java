package net.novauniverse.bedwars.game;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.commands.ImportBedwarsPreferences;
import net.novauniverse.bedwars.game.config.BedwarsConfig;
import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.entity.NPCType;
import net.novauniverse.bedwars.game.enums.ArmorType;
import net.novauniverse.bedwars.game.generator.GeneratorType;
import net.novauniverse.bedwars.game.generator.ItemGenerator;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.novauniverse.bedwars.game.shop.ItemShop;
import net.novauniverse.bedwars.game.shop.UpgradeShop;
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
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bedwars extends MapGame implements Listener {
	public static int WEAPON_SLOT_DEFAULT = 0;
	public static int TRACKER_SLOT_DEFAULT = 8;

	private boolean started;
	private boolean ended;

	private BedwarsConfig config;


	private Map<Player, ArmorType> hasArmor;
	private Map<Player, Integer> pickaxeTier;
	private Map<Player, Integer> axeTier;

	private List<Location> allowBreak;
	private List<BedwarsNPC> npcs;
	private List<BaseData> bases;

	private ItemShop itemShop;
	private UpgradeShop upgradeShop;

	private List<ItemGenerator> generators;

	private Task generatorTask;

	public Map<Player, ArmorType> getAllPlayersArmor() {
		return hasArmor;
	}

	public ArmorType getPlayerArmor(Player player) {
		return hasArmor.get(player);
	}

	public Map<Player, Integer> getAllPlayersPickaxeTier() {
		return pickaxeTier;
	}

	public int getPlayerPickaxeTier(Player player) {
		return pickaxeTier.get(player);
	}

	public Map<Player, Integer> getAllPlayersAxeTier() {
		return axeTier;
	}

	public int getPlayerAxeTier(Player player) {
		return axeTier.get(player);
	}

	public Bedwars() {
		super(NovaBedwars.getInstance());

		bases = new ArrayList<>();
		allowBreak = new ArrayList<>();
		npcs = new ArrayList<>();

		hasArmor = new HashMap<>();
		pickaxeTier = new HashMap<>();
		axeTier = new HashMap<>();

		generators = new ArrayList<>();

		itemShop = new ItemShop();
		upgradeShop = new UpgradeShop();

		generatorTask = new SimpleTask(getPlugin(), new Runnable() {
			@Override
			public void run() {
				generators.forEach(gen -> gen.countdown());
			}
		}, 20L);
	}

	public BedwarsConfig getConfig() {
		return config;
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		getAllPlayersAxeTier().putIfAbsent(e.getPlayer(), 0);
		getAllPlayersArmor().putIfAbsent(e.getPlayer(), ArmorType.NO_ARMOR);
		getAllPlayersPickaxeTier().putIfAbsent(e.getPlayer(), 0);
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

		/*
		 * List<ConfiguredBaseData> cfgBase = new ArrayList<>(config.getBases());
		 * TeamManager.getTeamManager().getTeams().forEach(team -> { if (cfgBase.size()
		 * > 0) { ConfiguredBaseData configuredBase = cfgBase.remove(0); BaseData base =
		 * configuredBase.toBaseData(getWorld(), team); bases.add(base);
		 * 
		 * BedwarsNPC itemShopNPC = new BedwarsNPC(base.getItemShopLocation(),
		 * NPCType.ITEMS); BedwarsNPC upgradesShopNPC = new
		 * BedwarsNPC(base.getItemShopLocation(), NPCType.UPGRADES);
		 * 
		 * itemShopNPC.spawn(); upgradesShopNPC.spawn();
		 * 
		 * npcs.add(itemShopNPC); npcs.add(upgradesShopNPC); } else {
		 * Log.error("Bedwars", "Not enough configured bases for team " +
		 * team.getDisplayName()); } });
		 */

		// TODO: Load from config
		int ironGeneratorTime = 4;
		int goldGeneratorTime = 10;
		int diamondGeneratorTime = 30;
		int emeraldGeneratorTime = 60;

		List<Team> teamsToSetup = new ArrayList<>(TeamManager.getTeamManager().getTeams());
		config.getBases().forEach(cfgBase -> {
			Team team = null;
			if (teamsToSetup.size() > 0) {
				team = teamsToSetup.remove(0);
			}

			BaseData base = cfgBase.toBaseData(getWorld(), team);

			generators.add(new ItemGenerator(base.getSpawnLocation(), GeneratorType.IRON, ironGeneratorTime, false));
			generators.add(new ItemGenerator(base.getSpawnLocation(), GeneratorType.GOLD, goldGeneratorTime, false));

			BedwarsNPC itemShopNPC = new BedwarsNPC(base.getItemShopLocation(), NPCType.ITEMS);
			itemShopNPC.spawn();
			npcs.add(itemShopNPC);

			BedwarsNPC upgradesShopNPC = new BedwarsNPC(base.getUpgradeShopLocation(), NPCType.UPGRADES);
			upgradesShopNPC.spawn();
			npcs.add(upgradesShopNPC);

			bases.add(base);
		});

		config.getDiamondGenerators().forEach(xyz -> generators.add(new ItemGenerator(xyz.toBukkitLocation(getWorld()), GeneratorType.DIAMOND, diamondGeneratorTime, true)));
		config.getEmeraldGenerators().forEach(xyz -> generators.add(new ItemGenerator(xyz.toBukkitLocation(getWorld()), GeneratorType.EMERALD, emeraldGeneratorTime, true)));

		if (teamsToSetup.size() > 0) {
			Log.error("Bedwars", "Not enough bases configured! " + teamsToSetup.size() + " teams does not have a base");
		}

		Bukkit.getServer().getOnlinePlayers().forEach(this::tpToSpectator);

		Bukkit.getServer().getOnlinePlayers().stream().filter(p -> players.contains(p.getUniqueId())).forEach(this::tpToBase);

		Task.tryStartTask(generatorTask);

		started = true;
		TextComponent starter = new TextComponent(ChatColor.YELLOW + "Click here to import");
		BaseComponent[] hovermessage = new BaseComponent[]{starter};

		TextComponent prefix = new TextComponent(ChatColor.GREEN + "Click ");

		TextComponent here = new TextComponent(ChatColor.GOLD.toString() + ChatColor.BOLD + "here");
		here.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hovermessage));
		here.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + ImportBedwarsPreferences.COMMAND_NAME));

		TextComponent command = new TextComponent("/" + ImportBedwarsPreferences.COMMAND_NAME);
		command.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
		command.setBold(true);

		TextComponent suffix = new TextComponent(" or do ");
		suffix.setColor(net.md_5.bungee.api.ChatColor.GREEN);

		TextComponent suffix2 = new TextComponent(" to import your Hypixel preferences.");
		suffix2.setColor(net.md_5.bungee.api.ChatColor.GREEN);


		players.stream().forEach(uuid -> {
			Player player = Bukkit.getPlayer(uuid);
			player.spigot().sendMessage(prefix, here, suffix, command, suffix2);
		});
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

		Task.tryStopTask(generatorTask);

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

		allowBreak.clear();

		ended = true;
	}

	public void tpToBase(Player player) {
		Log.trace("Bedwars", "tpToBase:" + player.getName());
		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		if (team != null) {
			BaseData base = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
			if (base != null) {
				player.setGameMode(GameMode.SURVIVAL);
				player.setHealth(player.getMaxHealth());
				player.setSaturation(20);
				player.setFoodLevel(20);
				PlayerUtils.clearPotionEffects(player);
				player.teleport(base.getSpawnLocation());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (started) {
			if (!allowBreak.contains(e.getBlock().getLocation())) {
				allowBreak.add(e.getBlock().getLocation());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
		Player player = e.getPlayer();

		if (e.getPlayer().getGameMode() != GameMode.SPECTATOR) {
			npcs.stream().filter(n -> n.getVillager().getUniqueId().equals(e.getRightClicked().getUniqueId())).findFirst().ifPresent(clickedNPC -> {
				new BukkitRunnable() {
					@Override
					public void run() {
						player.closeInventory();
						switch (clickedNPC.getType()) {
						case ITEMS:
							itemShop.display(player);
							break;

						case UPGRADES:
							upgradeShop.display(player);
							break;

						default:
							break;
						}
					}
				}.runTaskLater(getPlugin(), 1L);
			});
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e) {
		if (started) {
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
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

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onItemMerge(ItemMergeEvent e) {
		if (e.getEntity().hasMetadata(ItemGenerator.NO_MERGE_METADATA_KEY) || e.getTarget().hasMetadata(ItemGenerator.NO_MERGE_METADATA_KEY)) {
			e.setCancelled(true);
		}
	}
}