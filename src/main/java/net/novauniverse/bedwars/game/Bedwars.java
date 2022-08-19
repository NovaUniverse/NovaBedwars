package net.novauniverse.bedwars.game;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.commands.ImportBedwarsPreferences;
import net.novauniverse.bedwars.game.config.BedwarsConfig;
import net.novauniverse.bedwars.game.config.GeneratorUpgrade;
import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.entity.NPCType;
import net.novauniverse.bedwars.game.enums.ArmorType;
import net.novauniverse.bedwars.game.events.BedDestructionEvent;
import net.novauniverse.bedwars.game.generator.GeneratorType;
import net.novauniverse.bedwars.game.generator.ItemGenerator;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.novauniverse.bedwars.game.shop.ItemShop;
import net.novauniverse.bedwars.game.shop.UpgradeShop;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.ItemUtils;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;
import net.zeeraa.novacore.spigot.utils.RandomFireworkEffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Bedwars extends MapGame implements Listener {
	public static int WEAPON_SLOT_DEFAULT = 0;
	public static int TRACKER_SLOT_DEFAULT = 8;

	public static final float FIREBALL_YIELD = 1F;
	public static final float TNT_YIELD = 4F;

	public static final int RESPAWN_TIME_SECONDS = 10;

	private boolean started;
	private boolean ended;

	private int bedDestructionTime;

	private BedwarsConfig config;

	private Map<Player, ArmorType> hasArmor;
	private Map<Player, Integer> pickaxeTier;
	private Map<Player, Integer> axeTier;

	private Map<UUID, BukkitTask> respawnTasks;

	private List<Location> allowBreak;
	private List<BedwarsNPC> npcs;
	private List<BaseData> bases;
	private List<ItemGenerator> generators;
	private List<GeneratorUpgrade> generatorUpgrades;

	private ItemShop itemShop;
	private UpgradeShop upgradeShop;

	private Task countdownTask;

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

	public List<GeneratorUpgrade> getGeneratorUpgrades() {
		return generatorUpgrades;
	}

	public int getBedDestructionTime() {
		return bedDestructionTime;
	}
	
	public Bedwars() {
		super(NovaBedwars.getInstance());

		generatorUpgrades = new ArrayList<>();

		bases = new ArrayList<>();
		allowBreak = new ArrayList<>();
		npcs = new ArrayList<>();

		hasArmor = new HashMap<>();
		pickaxeTier = new HashMap<>();
		axeTier = new HashMap<>();

		respawnTasks = new HashMap<>();

		generators = new ArrayList<>();

		itemShop = new ItemShop();
		upgradeShop = new UpgradeShop();

		bedDestructionTime = Integer.MAX_VALUE;

		countdownTask = new SimpleTask(getPlugin(), () -> {
			if (bedDestructionTime > 0) {
				bedDestructionTime--;
				if (bedDestructionTime == 0) {
					bases.stream().filter(b -> b.hasBed()).forEach(base -> {
						base.setBed(false);
						base.getBedLocation().getBlock().breakNaturally();
						base.getOwner().getOnlinePlayers().forEach(player -> {
							VersionIndependentSound.WITHER_DEATH.play(player);
							VersionIndependentUtils.get().sendTitle(player, ChatColor.RED + TextUtils.ICON_WARNING + " Bed destroyed " + TextUtils.ICON_WARNING, ChatColor.RED + "You can no longer respawn", 0, 60, 20);
						});
					});
				}
			}

			generators.forEach(ItemGenerator::countdown);
			generatorUpgrades.forEach(GeneratorUpgrade::decrement);
			generatorUpgrades.stream().filter(GeneratorUpgrade::isFinished).forEach(upgrade -> {
				VersionIndependentSound.NOTE_PLING.broadcast();
				Bukkit.getServer().broadcastMessage(ChatColor.GREEN + upgrade.getType().getName() + " generators have been upgraded");
				generators.stream().filter(gen -> gen.getType() == upgrade.getType()).forEach(gen -> gen.decreaseDefaultTime(upgrade.getSpeedIncrement()));
			});
			generatorUpgrades.removeIf(GeneratorUpgrade::isFinished);
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
		return true;
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
		return true;
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

		generatorUpgrades = new ArrayList<>(config.getUpgrades());

		int ironGeneratorTime = config.getInitialIronTime();
		int goldGeneratorTime = config.getInitialGoldTime();
		int diamondGeneratorTime = config.getInitialDiamondTime();
		int emeraldGeneratorTime = config.getInitialEmeraldTime();

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

		VersionIndependentUtils.get().setGameRule(getWorld(), "keepInventory", "false");

		Bukkit.getServer().getOnlinePlayers().forEach(this::tpToSpectator);

		Bukkit.getServer().getOnlinePlayers().stream().filter(p -> players.contains(p.getUniqueId())).forEach(this::tpToBase);

		Task.tryStartTask(countdownTask);

		sendStartMessage();

		getWorld().setDifficulty(Difficulty.PEACEFUL);

		started = true;
		sendBeginEvent();
	}

	public void sendStartMessage() {
		TextComponent starter = new TextComponent(ChatColor.YELLOW + "Click here to import");
		BaseComponent[] hovermessage = new BaseComponent[] { starter };

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

		Bukkit.getServer().getOnlinePlayers().stream().filter(p -> isPlayerInGame(p)).forEach(player -> player.spigot().sendMessage(prefix, here, suffix, command, suffix2));
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

		respawnTasks.values().forEach(task -> task.cancel());
		respawnTasks.clear();

		Task.tryStopTask(countdownTask);

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
				PlayerUtils.clearPlayerInventory(player);
				player.teleport(base.getSpawnLocation());

				// TODO: Give player items
				player.getInventory().setItem(WEAPON_SLOT_DEFAULT, new ItemBuilder(VersionIndependentMaterial.WOODEN_SWORD).setUnbreakable(true).build());
			}
		}
	}

	public void tryCancelRespawn(OfflinePlayer player) {
		UUID uuid = player.getUniqueId();
		if (respawnTasks.containsKey(uuid)) {
			respawnTasks.get(uuid).cancel();
			respawnTasks.remove(uuid);
		}
	}

	public void handlePlayerRespawnOrJoin(Player player) {
		PlayerUtils.clearPlayerInventory(player);
		player.setGameMode(GameMode.SPECTATOR);
		tpToSpectator(player);

		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		if (team != null) {
			BaseData base = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
			if (base != null) {
				if (base.hasBed()) {
					if (players.contains(player.getUniqueId())) {
						tryCancelRespawn(player);
						VersionIndependentUtils.get().sendTitle(player, "", ChatColor.AQUA + "Respawning in " + RESPAWN_TIME_SECONDS + " seconds", 0, 60, 20);
						BukkitTask task = new BukkitRunnable() {
							@Override
							public void run() {
								Log.debug("Bedwars", "Respawn timer over for " + player.getName());
								if (player.isOnline()) {
									tpToBase(player);
								}
							}
						}.runTaskLater(getPlugin(), RESPAWN_TIME_SECONDS * 20L);
						respawnTasks.put(player.getUniqueId(), task);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!started) {
			return;
		}

		Player player = e.getEntity();

		PlayerUtils.clearPlayerInventory(player);
		e.setDroppedExp(0);
		e.getDrops().clear();
		player.spigot().respawn();

		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		if (team != null) {
			if (players.contains(player.getUniqueId())) {
				BaseData base = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
				if (base != null) {
					if (!base.hasBed()) {
						eliminatePlayer(player, player.getKiller(), PlayerEliminationReason.DEATH);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if (!started) {
			return;
		}

		Player player = e.getPlayer();

		e.setRespawnLocation(getActiveMap().getSpectatorLocation());
		handlePlayerRespawnOrJoin(player);
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
	public void onItemSpawn(ItemSpawnEvent e) {
		if (started && !ended) {
			if (VersionIndependentUtils.get().isBed(e.getEntity().getItemStack().getType())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if (e.getItemDrop().getItemStack().getType() == VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion()) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e) {
		if (started) {
			Player player = e.getPlayer();

			if (player.getGameMode() != GameMode.CREATIVE) {
				boolean allow = false;
				Block block = e.getBlock();
				if (VersionIndependentUtils.get().isBed(block)) {
					BaseData ownerBase = bases.stream().filter(base -> LocationUtils.isSameBlock(base.getBedLocation(), block.getLocation())).findFirst().orElse(null);
					if (ownerBase == null) {
						for (BlockFace face : BlockFace.values()) {
							Location location2 = LocationUtils.addFaceMod(block.getLocation().clone(), face);

							ownerBase = bases.stream().filter(base -> LocationUtils.isSameBlock(base.getBedLocation(), location2)).findFirst().orElse(null);
							if (ownerBase != null) {
								break;
							}
						}
					}

					if (ownerBase != null) {
						Team team = TeamManager.getTeamManager().getPlayerTeam(player);
						if (team != null) {
							if (ownerBase.getOwner().equals(team)) {
								player.sendMessage(ChatColor.RED + "You can't break your own bed");
								e.setCancelled(true);
								return;
							}
						}

						// We broke the bed
						Bukkit.getServer().broadcastMessage(team.getTeamColor() + "" + ChatColor.BOLD + team.getDisplayName() + ChatColor.GOLD + ChatColor.BOLD + " broke " + ownerBase.getOwner().getTeamColor() + ChatColor.BOLD + ownerBase.getOwner().getDisplayName() + ChatColor.GOLD + ChatColor.BOLD + "'s bed");
						ownerBase.getOwner().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Bed Broken> " + team.getTeamColor() + "" + ChatColor.BOLD + team.getDisplayName() + ChatColor.RED + ChatColor.BOLD + " broke your bed");
						ownerBase.setBed(false);
						ownerBase.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.WITHER_DEATH.play(p);
							VersionIndependentUtils.get().sendTitle(p, ChatColor.RED + TextUtils.ICON_WARNING + " Bed destroyed " + TextUtils.ICON_WARNING, ChatColor.RED + "You can no longer respawn", 0, 60, 20);
						});

						Event bedDestructionEvent = new BedDestructionEvent(ownerBase.getOwner(), team, player);
						Bukkit.getServer().getPluginManager().callEvent(bedDestructionEvent);
					}

					allow = true;
				} else if (allowBreak.contains(block.getLocation())) {
					allow = true;
				}

				if (!allow) {
					e.setCancelled(true);
					player.sendMessage(ChatColor.RED + "You can only break blocks placed by players");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (VersionIndependentUtils.get().isBed(e.getClickedBlock())) {
				e.setCancelled(true);
			}
		}

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
			Player player = e.getPlayer();
			if (VersionIndependentUtils.get().isInteractEventMainHand(e)) {
				Material type = VersionIndependentUtils.get().getItemInMainHand(player).getType();
				if (type == Material.FIREBALL) {
					e.setCancelled(true);

					ItemUtils.removeOneFromHand(player);

					final Fireball fireball = player.launchProjectile(Fireball.class);
					fireball.setVelocity(player.getLocation().getDirection().multiply(2));
					fireball.setBounce(false);
					fireball.setYield(Bedwars.FIREBALL_YIELD);
					fireball.setIsIncendiary(false);
					fireball.setCustomName(ChatColor.GOLD + "Fireball");
					fireball.setCustomNameVisible(false);
					fireball.setShooter(player);
				} else if (type == Material.TNT && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					e.setCancelled(true);

					ItemUtils.removeOneFromHand(player);

					BlockFace face = e.getBlockFace();
					Location location = e.getClickedBlock().getLocation().clone().add(face.getModX(), face.getModY(), face.getModZ());
					TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
					tnt.setYield(Bedwars.TNT_YIELD);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerEliminated(PlayerEliminatedEvent e) {
		OfflinePlayer player = e.getPlayer();
		tryCancelRespawn(player);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent e) {
		if (started && !ended) {
			e.blockList().removeIf(block -> !allowBreak.contains(block.getLocation()));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockExplode(BlockExplodeEvent e) {
		if (started && !ended) {
			e.blockList().removeIf(block -> !allowBreak.contains(block.getLocation()));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		tryCancelRespawn(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (started && !ended) {
			handlePlayerRespawnOrJoin(player);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onItemMerge(ItemMergeEvent e) {
		if (e.getEntity().hasMetadata(ItemGenerator.NO_MERGE_METADATA_KEY) || e.getTarget().hasMetadata(ItemGenerator.NO_MERGE_METADATA_KEY)) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onItemDespawn(ItemDespawnEvent e) {
		if (e.getEntity().hasMetadata(ItemGenerator.NO_MERGE_METADATA_KEY)) {
			e.getEntity().setTicksLived(1);
			e.setCancelled(true);
		}
	}
}