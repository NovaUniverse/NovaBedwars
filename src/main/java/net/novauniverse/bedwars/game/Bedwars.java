package net.novauniverse.bedwars.game;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.commands.ImportBedwarsPreferences;
import net.novauniverse.bedwars.game.config.BedwarsConfig;
import net.novauniverse.bedwars.game.config.event.BedBreak;
import net.novauniverse.bedwars.game.config.event.BedwarsEvent;
import net.novauniverse.bedwars.game.config.event.EndGame;
import net.novauniverse.bedwars.game.config.event.GeneratorUpgrade;
import net.novauniverse.bedwars.game.entity.BedwarsNPC;
import net.novauniverse.bedwars.game.entity.CustomFireball;
import net.novauniverse.bedwars.game.entity.NPCType;
import net.novauniverse.bedwars.game.entity.dragon.BedwarsDragon;
import net.novauniverse.bedwars.game.enums.ArmorType;
import net.novauniverse.bedwars.game.enums.Reason;
import net.novauniverse.bedwars.game.enums.ShopItem;
import net.novauniverse.bedwars.game.enums.Trap;
import net.novauniverse.bedwars.game.events.AttemptItemBuyEvent;
import net.novauniverse.bedwars.game.events.BedDestructionEvent;
import net.novauniverse.bedwars.game.events.PlayerKilledEvent;
import net.novauniverse.bedwars.game.generator.GeneratorType;
import net.novauniverse.bedwars.game.generator.ItemGenerator;
import net.novauniverse.bedwars.game.holder.SpectatorHolder;
import net.novauniverse.bedwars.game.modules.preferences.BedwarsPreferenceManager;
import net.novauniverse.bedwars.game.modules.preferences.PreferenceAPIRequestCallback;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.novauniverse.bedwars.game.shop.ItemShop;
import net.novauniverse.bedwars.game.shop.UpgradeShop;
import net.novauniverse.bedwars.utils.CountdownTask;
import net.novauniverse.bedwars.utils.CountdownTaskManager;
import net.novauniverse.bedwars.utils.InventoryUtils;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.DelayedRunner;
import net.zeeraa.novacore.commons.utils.RandomGenerator;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.DeathType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.abstraction.manager.CustomSpectatorManager;
import net.zeeraa.novacore.spigot.abstraction.particle.NovaParticleEffect;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.GameTrigger;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.TriggerFlag;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTracker;
import net.zeeraa.novacore.spigot.module.modules.cooldown.CooldownManager;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.ChatColorRGBMapper;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.ItemUtils;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;
import net.zeeraa.novacore.spigot.utils.RandomFireworkEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFireball;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Bed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Bedwars extends MapGame implements Listener {
	public static int WEAPON_SLOT_DEFAULT = 0;
	public static int TRACKER_SLOT_DEFAULT = 8;

	public static final int SPAWN_PROTECTION_RADIUS = 4;

	public static final int CHARGE_TIME_TICKS = 500;

	public static final float FIREBALL_YIELD = 2F;
	public static final float TNT_YIELD = 4F;

	public static final int RESPAWN_TIME_SECONDS = 5;
	public static final int INVENCIBILITY_TIME_SECONDS = 3;
	public static final int KILL_CREDIT_TIMER_SECONDS = 30;

	public static final int MAX_TRAP_AMOUNT = 3;
	public static final ItemStack TELEPORT_COMPASS = new ItemBuilder(Material.COMPASS).setName(ChatColor.GOLD + "" + ChatColor.BOLD + "Teleport to player").build();

	public static final String FIREBALL_COOLDOWN_ID = "fireball_cooldown";

	private boolean started;
	private boolean ended;

	private boolean endGameEnabled;

	private BedwarsConfig config;

	private Map<Player, ArmorType> armor;
	private Map<Player, Integer> pickaxeTier;
	private Map<Player, Integer> axeTier;
	private Map<Player, Integer> invencibility;

	private Map<UUID, BukkitTask> respawnTasks;
	private Map<Player, Entity> lastDamager;

	private List<Location> allowBreak;
	private List<BedwarsNPC> npcs;
	private List<BaseData> bases;
	private List<ItemGenerator> generators;
	private List<BedwarsEvent> events;
	private List<Hologram> baseNameHolograms;
	private List<UUID> armorHidden;

	private List<UUID> haveWeClearedThisMF;

	private List<UUID> eliminatedPlayers;

	private List<Entity> dragons;

	private ItemShop itemShop;
	private UpgradeShop upgradeShop;

	private Task tickTask;
	private Task countdownTask;
	private Task generatorTask;
	private Task npcFixTask;
	private Task particleTask;
	private Task armorCheckTask;
	private Task compassTask;
	private Task tntParticleTask;
	private Task playerUpdateTask;
	private Task spectatorUpdateTask;

	private Task updatePreferencesTask;

	private Task worldUpdateTask;
	private Task baseTask;

	public Map<Player, ArmorType> getAllPlayersArmor() {
		return armor;
	}

	public List<BaseData> getBases() {
		return bases;
	}

	public ArmorType getPlayerArmor(Player player) {
		return armor.get(player);
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

	public List<BedwarsEvent> getEvents() {
		return events;
	}

	@SuppressWarnings("deprecation")
	public Bedwars() {
		super(NovaBedwars.getInstance());
		events = new ArrayList<>();

		bases = new ArrayList<>();
		allowBreak = new ArrayList<>();
		npcs = new ArrayList<>();

		armor = new HashMap<>();
		pickaxeTier = new HashMap<>();
		axeTier = new HashMap<>();
		invencibility = new HashMap<>();

		respawnTasks = new HashMap<>();
		lastDamager = new HashMap<>();

		generators = new ArrayList<>();

		armorHidden = new ArrayList<>();

		baseNameHolograms = new ArrayList<>();

		haveWeClearedThisMF = new ArrayList<>();

		eliminatedPlayers = new ArrayList<>();

		dragons = new ArrayList<>();

		itemShop = new ItemShop();
		upgradeShop = new UpgradeShop();

		tickTask = new SimpleTask(getPlugin(), () -> {
			npcs.forEach(BedwarsNPC::lookAtPlayer);
			dragons.forEach(entity -> {
				if (((CraftEntity) entity).getHandle() instanceof BedwarsDragon) {
					BedwarsDragon bwd = (BedwarsDragon) ((CraftEntity) entity).getHandle();
					bwd.tick();
				}
			});
		}, 1L);
		npcFixTask = new SimpleTask(getPlugin(), () -> npcs.forEach(BedwarsNPC::checkVillager), 20L);

		tntParticleTask = new SimpleTask(getPlugin(), () -> Bukkit.getServer().getOnlinePlayers().stream().filter(p -> p.getInventory().contains(Material.TNT)).forEach(player -> NovaParticleEffect.REDSTONE.display(player.getLocation().add(0D, 2.5D, 0D))), 3L);
		particleTask = new SimpleTask(getPlugin(), () -> Bukkit.getServer().getOnlinePlayers().stream().filter(p -> p.hasPotionEffect(PotionEffectType.INVISIBILITY) && !CustomSpectatorManager.isSpectator(p) && p.isOnGround()).forEach(p -> NovaParticleEffect.FOOTSTEP.display(p.getLocation().add(0D, 2.05D, 0D))), 5L);

		compassTask = new SimpleTask(getPlugin(), () -> Bukkit.getServer().getOnlinePlayers().stream().filter(p -> TeamManager.getTeamManager().hasTeam(p)).forEach(player -> {
			Team team = TeamManager.getTeamManager().getPlayerTeam(player);
			BaseData base = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
			if (base != null) {
				player.setCompassTarget(base.getBedLocation());
			}
		}), 10L);

		armorCheckTask = new SimpleTask(getPlugin(), () -> Bukkit.getServer().getOnlinePlayers().stream().filter(this::isPlayerInGame).forEach(player -> {

			if (!CustomSpectatorManager.isSpectator(player)) {
				if (armorHidden.contains(player.getUniqueId())) {
					if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						armorHidden.remove(player.getUniqueId());
						giveArmorAndTools(player);
						PacketPlayOutEntityEquipment helmetPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), 1, CraftItemStack.asNMSCopy(player.getInventory().getHelmet()));
						PacketPlayOutEntityEquipment chestplatePacket = new PacketPlayOutEntityEquipment(player.getEntityId(), 2, CraftItemStack.asNMSCopy(player.getInventory().getChestplate()));
						PacketPlayOutEntityEquipment leggingsPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), 3, CraftItemStack.asNMSCopy(player.getInventory().getLeggings()));
						PacketPlayOutEntityEquipment bootsPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), 4, CraftItemStack.asNMSCopy(player.getInventory().getBoots()));
						for (Player online : Bukkit.getOnlinePlayers()) {
							if (!CustomSpectatorManager.isSpectator(online) && !TeamManager.getTeamManager().isInSameTeam(player.getUniqueId(), online.getUniqueId())) {
								VersionIndependentUtils.get().sendPacket(online, helmetPacket);
								VersionIndependentUtils.get().sendPacket(online, chestplatePacket);
								VersionIndependentUtils.get().sendPacket(online, leggingsPacket);
								VersionIndependentUtils.get().sendPacket(online, bootsPacket);
							}
						}
					}
				} else if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					armorHidden.add(player.getUniqueId());
					PacketPlayOutEntityEquipment helmetPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), 1, null);
					PacketPlayOutEntityEquipment chestplatePacket = new PacketPlayOutEntityEquipment(player.getEntityId(), 2, null);
					PacketPlayOutEntityEquipment leggingsPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), 3, null);
					PacketPlayOutEntityEquipment bootsPacket = new PacketPlayOutEntityEquipment(player.getEntityId(), 4, null);
					for (Player online : Bukkit.getOnlinePlayers()) {
						if (!CustomSpectatorManager.isSpectator(online) && !TeamManager.getTeamManager().isInSameTeam(player.getUniqueId(), online.getUniqueId())) {
							VersionIndependentUtils.get().sendPacket(online, helmetPacket);
							VersionIndependentUtils.get().sendPacket(online, chestplatePacket);
							VersionIndependentUtils.get().sendPacket(online, leggingsPacket);
							VersionIndependentUtils.get().sendPacket(online, bootsPacket);
						}
					}
				}
			}
		}), 5L);
		/*
		 * chargeTask = new SimpleTask(getPlugin(), () ->
		 * world.getEntities().stream().filter(entity -> ((CraftEntity)
		 * entity).getHandle() instanceof BedwarsDragon).forEach(entity -> {
		 * BedwarsDragon dragon = (BedwarsDragon) ((CraftEntity) entity).getHandle(); if
		 * (!dragon.isCharging()) { dragon.setTarget(null); } if (dragon.getChargeTime()
		 * <= 0) { if (dragon.target != null) { if (!dragon.isCharging()) {
		 * dragon.resetCharge(); dragon.setTarget(null); return; } } else { double
		 * toCheck = RandomGenerator.generateDouble(0,200); if (toCheck <=
		 * dragon.getChargeTime()*-1) {
		 * dragon.chargePlayer(ListUtils.shuffleWithRandom(world.getPlayers(), new
		 * Random()).get(0)); } } } dragon.decreaseCharge(); }), 1L);
		 * 
		 */

		generatorTask = new SimpleTask(getPlugin(), () -> {
			bases.forEach(baseData -> baseData.getBaseGenerator().tick());
		}, 1L);
		countdownTask = new SimpleTask(getPlugin(), () -> {
			events.forEach(BedwarsEvent::decrement);
			generators.forEach(ItemGenerator::countdown);
			events.stream().filter(BedwarsEvent::isFinished).forEach(event -> {
				VersionIndependentSound.NOTE_PLING.broadcast();
				if (event instanceof GeneratorUpgrade) {
					GeneratorUpgrade gu = (GeneratorUpgrade) event;
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + gu.getType().getName() + " generators have been upgraded");
					generators.stream().filter(gen -> gen.getType() == gu.getType()).forEach(gen -> gen.decreaseDefaultTime(gu.getSpeedIncrement()));
				} else if (event instanceof BedBreak) {
					Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Bed Destruction> All beds have been destroyed.");
					bases.stream().filter(BaseData::hasBed).forEach(base -> {
						base.setBed(false);
						base.getBedLocation().getBlock().breakNaturally();

						base.getOwner().getOnlinePlayers().forEach(player -> {
							VersionIndependentSound.WITHER_DEATH.play(player);
							VersionIndependentUtils.get().sendTitle(player, ChatColor.RED + TextUtils.ICON_WARNING + " Bed destroyed " + TextUtils.ICON_WARNING, ChatColor.RED + "You can no longer respawn", 0, 60, 20);
						});
					});
				} else if (event instanceof EndGame) {
					Bukkit.broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "The End Game has STARTED!");
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
					}
					startEndGame();
				}

			});
			events.removeIf(BedwarsEvent::isFinished);
		}, 20L);
		playerUpdateTask = new SimpleTask(getPlugin(), () -> players.forEach(uuid -> {
			if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
				if (!CustomSpectatorManager.isSpectator(Bukkit.getPlayer(uuid))) {
					if (TeamManager.getTeamManager().hasTeam(uuid)) {
						removeExcessItems(Bukkit.getPlayer(uuid));
						updateEnchantment(Bukkit.getPlayer(uuid));
						addWoodSword(Bukkit.getPlayer(uuid));
						addEffects(Bukkit.getPlayer(uuid));
						trap(Bukkit.getPlayer(uuid));
					}

				}
			}
		}), 2L);

		spectatorUpdateTask = new SimpleTask(getPlugin(), () -> {
			List<UUID> avaliablePlayers = new ArrayList<>();
			for (UUID id : players) {
				OfflinePlayer op = Bukkit.getOfflinePlayer(id);
				if (op.isOnline()) {
					if (!CustomSpectatorManager.isSpectator(op.getPlayer())) {
						avaliablePlayers.add(op.getPlayer().getUniqueId());
					}
				}
				SpectatorHolder.update(avaliablePlayers);
			}

		}, 2L);

		worldUpdateTask = new SimpleTask(getPlugin(), () -> {
			for (Entity e : getWorld().getEntities()) {
				if (e.getType() == EntityType.DROPPED_ITEM) {
					Item i = (Item) e;
					if (i.getItemStack().getType() == Material.BED) {
						i.remove();
					}
				}
			}
			if (allDragonsDead()) {
				if (!doingMonologue) {
					doingMonologue = true;
					Bukkit.broadcastMessage(ChatColor.RED + "wait hold on did you really kill all dragons?");

					DelayedRunner.runDelayed(() -> {
						Bukkit.broadcastMessage(ChatColor.DARK_RED + "well that was a mistake");
						DelayedRunner.runDelayed(() -> {
							Bukkit.broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "well have fun with these dragons");
							spawnDruggedDragons();
						}, 80L);
					}, 80L);
				}

			}
		}, 20L);
		baseTask = new SimpleTask(getPlugin(), () -> {
			for (BaseData data : getBases()) {
				if (data.hasHealPool()) {
					for (int i = 0; i < 500; i++) {
						Location location = data.getSpawnLocation().clone().add(RandomGenerator.generateDouble(-config.getHealPoolRadius(), config.getHealPoolRadius()), RandomGenerator.generateDouble(-config.getHealPoolRadius(), config.getHealPoolRadius()), RandomGenerator.generateDouble(-config.getHealPoolRadius(), config.getHealPoolRadius()));
						data.getSpawnLocation().getWorld().playEffect(location, Effect.HAPPY_VILLAGER, 0);
					}
				}
			}
		}, 40);
		updatePreferencesTask = new SimpleTask(getPlugin(), () -> {
			for (UUID id : getPlayers()) {
				updateSynced(id);
			}
		}, 20*60L);
	}

	private boolean doingMonologue = false;

	public Map<Player, Integer> getAllPlayersInvencibility() {
		return invencibility;
	}

	public Integer getPlayerInvencibility(Player player) {
		return invencibility.get(player);
	}

	public Map<Player, Entity> getAllPlayersLastDamager() {
		return lastDamager;
	}

	public Entity getPlayerLastDamager(Player player) {
		return lastDamager.get(player);
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

		getWorld().getWorldBorder().reset();

		GameTrigger endGameStart = new GameTrigger("startendgame", (gameTrigger, triggerFlag) -> {
			startEndGame();
		});
		endGameStart.setDescription("Start the sudden death event.");
		endGameStart.addFlag(TriggerFlag.RUN_ONLY_ONCE);
		addTrigger(endGameStart);

		GameTrigger killDragons = new GameTrigger("killdragons", ((gameTrigger, triggerFlag) -> {
			killAllDragons();
		}));
		killDragons.setDescription("Kill all dragons");
		addTrigger(killDragons);

		ModuleManager.disable(CompassTracker.class);

		BedwarsConfig config = (BedwarsConfig) getActiveMap().getMapData().getMapModule(BedwarsConfig.class);
		if (config == null) {
			Log.fatal("NovaBedwars", "Map " + this.getActiveMap().getMapData().getMapName() + " has no config");
			Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Bedwars has gone into an error and has to be stopped");
			endGame(GameEndReason.ERROR);
			return;
		}
		this.config = config;

		events = new ArrayList<>(config.getEvents());
		int diamondGeneratorTime = config.getInitialDiamondTime();
		int emeraldGeneratorTime = config.getInitialEmeraldTime();

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!haveWeClearedThisMF.contains(player.getUniqueId())) {
				player.getEnderChest().clear();
				haveWeClearedThisMF.add(player.getUniqueId());
			}
			setUpPlayer(player);
		}

		List<Team> teamsToSetup = new ArrayList<>(TeamManager.getTeamManager().getTeams());
		config.getBases().forEach(cfgBase -> {
			Team team = null;
			if (teamsToSetup.size() > 0) {
				team = teamsToSetup.remove(0);
			}

			BaseData base = cfgBase.toBaseData(getWorld(), team);

			BedwarsNPC itemShopNPC = new BedwarsNPC(base.getItemShopLocation(), NPCType.ITEMS);
			itemShopNPC.spawn();
			npcs.add(itemShopNPC);

			BedwarsNPC upgradesShopNPC = new BedwarsNPC(base.getUpgradeShopLocation(), NPCType.UPGRADES);
			upgradesShopNPC.spawn();
			npcs.add(upgradesShopNPC);

			String teamName = ChatColor.RED + "" + ChatColor.BOLD + TextUtils.ICON_WARNING + " No Team " + TextUtils.ICON_WARNING;
			if (team == null) {
				base.setBed(false);
				base.getBedLocation().getBlock().breakNaturally();
			} else {
				teamName = team.getTeamColor() + "" + ChatColor.BOLD + team.getDisplayName();
			}

			Hologram hologram = HologramsAPI.createHologram(getPlugin(), base.getBedLocation().clone().add(0.0D, 6.0D, 0.0D));
			hologram.appendTextLine(teamName);
			baseNameHolograms.add(hologram);

			bases.add(base);
		});

		config.getDiamondGenerators().forEach(xyz -> generators.add(new ItemGenerator(xyz.toBukkitLocation(getWorld()), GeneratorType.DIAMOND, diamondGeneratorTime, true, false)));
		config.getEmeraldGenerators().forEach(xyz -> generators.add(new ItemGenerator(xyz.toBukkitLocation(getWorld()), GeneratorType.EMERALD, emeraldGeneratorTime, true, false)));

		if (teamsToSetup.size() > 0) {
			Log.error("Bedwars", "Not enough bases configured! " + teamsToSetup.size() + " teams does not have a base");
		}

		VersionIndependentUtils.get().setGameRule(getWorld(), "keepInventory", "false");
		VersionIndependentUtils.get().setGameRule(getWorld(), "doDaylightCycle", "true");
		getWorld().setTime(6000);
		Bukkit.getServer().getOnlinePlayers().forEach(this::tpToSpectator);

		Bukkit.getServer().getOnlinePlayers().stream().filter(p -> players.contains(p.getUniqueId())).forEach(this::tpToBase);
		Bukkit.getServer().getOnlinePlayers().stream().filter(p -> !players.contains(p.getUniqueId())).forEach(this::setEliminatedSpectator);
		Task.tryStartTask(countdownTask);
		Task.tryStartTask(generatorTask);
		Task.tryStartTask(tickTask);
		Task.tryStartTask(npcFixTask);
		Task.tryStartTask(particleTask);
		Task.tryStartTask(armorCheckTask);
		Task.tryStartTask(compassTask);
		Task.tryStartTask(tntParticleTask);
		Task.tryStartTask(playerUpdateTask);
		Task.tryStartTask(spectatorUpdateTask);
		Task.tryStartTask(updatePreferencesTask);
		Task.tryStartTask(worldUpdateTask);
		Task.tryStartTask(baseTask);

		sendStartMessage();

		getWorld().setDifficulty(Difficulty.HARD);
		VersionIndependentUtils.get().setGameRule(getWorld(), "doMobSpawning", "false");

		started = true;

		endGameEnabled = false;

		// set up locations that cannot be broken

		for (BaseData data : getBases()) {
			for (int x = data.getSpawnLocation().getBlockX() - SPAWN_PROTECTION_RADIUS; x <= data.getSpawnLocation().getBlockX() + SPAWN_PROTECTION_RADIUS; x++) {
				for (int y = data.getSpawnLocation().getBlockY() - SPAWN_PROTECTION_RADIUS; y <= data.getSpawnLocation().getBlockY() + SPAWN_PROTECTION_RADIUS; y++) {
					for (int z = data.getSpawnLocation().getBlockZ() - SPAWN_PROTECTION_RADIUS; z <= data.getSpawnLocation().getBlockZ() + SPAWN_PROTECTION_RADIUS; z++) {
						safeLocations.add(world.getBlockAt(x, y, z).getLocation());
					}
				}
			}
			for (int x = data.getItemShopLocation().getBlockX() - SPAWN_PROTECTION_RADIUS; x <= data.getItemShopLocation().getBlockX() + SPAWN_PROTECTION_RADIUS; x++) {
				for (int y = data.getItemShopLocation().getBlockY() - SPAWN_PROTECTION_RADIUS; y <= data.getItemShopLocation().getBlockY() + SPAWN_PROTECTION_RADIUS; y++) {
					for (int z = data.getItemShopLocation().getBlockZ() - SPAWN_PROTECTION_RADIUS; z <= data.getItemShopLocation().getBlockZ() + SPAWN_PROTECTION_RADIUS; z++) {
						safeLocations.add(world.getBlockAt(x, y, z).getLocation());
					}
				}
			}
			for (int x = data.getUpgradeShopLocation().getBlockX() - SPAWN_PROTECTION_RADIUS; x <= data.getUpgradeShopLocation().getBlockX() + SPAWN_PROTECTION_RADIUS; x++) {
				for (int y = data.getUpgradeShopLocation().getBlockY() - SPAWN_PROTECTION_RADIUS; y <= data.getUpgradeShopLocation().getBlockY() + SPAWN_PROTECTION_RADIUS; y++) {
					for (int z = data.getUpgradeShopLocation().getBlockZ() - SPAWN_PROTECTION_RADIUS; z <= data.getUpgradeShopLocation().getBlockZ() + SPAWN_PROTECTION_RADIUS; z++) {
						safeLocations.add(world.getBlockAt(x, y, z).getLocation());
					}
				}
			}
		}
		for (ItemGenerator generator : generators) {
			for (int x = generator.getLocation().getBlockX() - SPAWN_PROTECTION_RADIUS; x <= generator.getLocation().getBlockX() + SPAWN_PROTECTION_RADIUS; x++) {
				for (int y = generator.getLocation().getBlockY() - SPAWN_PROTECTION_RADIUS; y <= generator.getLocation().getBlockY() + SPAWN_PROTECTION_RADIUS; y++) {
					for (int z = generator.getLocation().getBlockZ() - SPAWN_PROTECTION_RADIUS; z <= generator.getLocation().getBlockZ() + SPAWN_PROTECTION_RADIUS; z++) {
						safeLocations.add(world.getBlockAt(x, y, z).getLocation());
					}
				}
			}
		}

		sendBeginEvent();
	}

	public boolean hasBed(Player player) {
		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		if (team != null) {
			BaseData base = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
			if (base != null) {
				return base.hasBed();
			}
		}
		return false;
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

		Bukkit.getServer().getOnlinePlayers().stream().filter(this::isPlayerInGame).forEach(player -> player.spigot().sendMessage(prefix, here, suffix, command, suffix2));
	}

	@Override
	public void tpToSpectator(Player player) {

		PlayerUtils.clearPlayerInventory(player);
		PlayerUtils.resetMaxHealth(player);
		PlayerUtils.resetPlayerXP(player);
		player.teleport(getActiveMap().getSpectatorLocation());
	}

	@Override
	public void onEnd(GameEndReason reason) {
		if (ended || !started) {
			return;
		}

		respawnTasks.values().forEach(BukkitTask::cancel);
		respawnTasks.clear();

		killAllDragons();

		Task.tryStopTask(countdownTask);
		Task.tryStopTask(generatorTask);
		Task.tryStopTask(tickTask);
		Task.tryStopTask(npcFixTask);
		Task.tryStopTask(particleTask);
		Task.tryStopTask(armorCheckTask);
		Task.tryStopTask(compassTask);
		Task.tryStopTask(tntParticleTask);
		Task.tryStopTask(playerUpdateTask);
		Task.tryStopTask(spectatorUpdateTask);
		Task.tryStopTask(updatePreferencesTask);
		Task.tryStopTask(worldUpdateTask);
		Task.tryStopTask(baseTask);

		baseNameHolograms.forEach(Hologram::delete);

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
		for (UUID id : getPlayers()) {
			updateSynced(id);
		}
		itemShop.destroy();

		allowBreak.clear();

		ended = true;
	}

	private void updateSynced(UUID id) {
		AsyncManager.runSync(() -> BedwarsPreferenceManager.getInstance().savePreferences(Bukkit.getPlayer(id), (success, exception) -> {
			// TODO: you do th is shit anton
		}));
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
				player.setFallDistance(0);
				PlayerUtils.clearPotionEffects(player);
				PlayerUtils.clearPlayerInventory(player);
				player.setFlySpeed(0.1f);
				VersionIndependentUtils.get().setCustomSpectator(player, false);
				player.setVelocity(new Vector(0, 0, 0));
				player.teleport(base.getSpawnLocation());

				player.getInventory().setItem(Bedwars.WEAPON_SLOT_DEFAULT, new ItemBuilder(VersionIndependentMaterial.WOODEN_SWORD).setUnbreakable(true).build());
				player.getInventory().setItem(Bedwars.TRACKER_SLOT_DEFAULT, new ItemBuilder(Material.COMPASS).build());
				giveArmorAndTools(player);
				invencibility.put(player, INVENCIBILITY_TIME_SECONDS);
				new BukkitRunnable() {

					@Override
					public void run() {
						invencibility.putIfAbsent(player, INVENCIBILITY_TIME_SECONDS);
						if (invencibility.get(player) == 0) {

							cancel();
						} else {
							invencibility.put(player, invencibility.get(player) - 1);
						}
					}
				}.runTaskTimer(NovaBedwars.getInstance(), 0, 20);
			}
		}
	}

	public void addEffects(Player player) {
		if (!CustomSpectatorManager.isSpectator(player) && TeamManager.getTeamManager().hasTeam(player.getUniqueId())) {
			Team team = TeamManager.getTeamManager().getPlayerTeam(player);
			BaseData baseData = bases.stream().filter(base -> base.getOwner().equals(team)).findFirst().orElse(null);
			if (baseData.getHasteLevel() != 0) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, baseData.getHasteLevel() - 1, false, false), true);
			}
			if (baseData.hasHealPool()) {
				if (baseData.getSpawnLocation().distance(player.getLocation()) <= config.getHealPoolRadius()) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (5 * 20) + 2, 0, false, false), true);
				}
			}

		}
	}

	public void giveArmorAndTools(Player player) {
		Color color = Color.WHITE;

		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		if (team != null) {
			color = ChatColorRGBMapper.chatColorToRGBColorData(team.getTeamColor()).toBukkitColor();
		}

		player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setLeatherArmorColor(color).setUnbreakable(true).build());
		player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(color).setUnbreakable(true).build());
		player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherArmorColor(color).setUnbreakable(true).build());
		player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherArmorColor(color).setUnbreakable(true).build());

		updatePlayerItems(player);
	}

	public void tryCancelRespawn(OfflinePlayer player) {
		UUID uuid = player.getUniqueId();
		if (respawnTasks.containsKey(uuid)) {
			respawnTasks.get(uuid).cancel();
			respawnTasks.remove(uuid);

		}
	}

	public void setSpectator(Player player) {
		PlayerUtils.clearPlayerInventory(player);
		tpToSpectator(player);
		player.setFlySpeed(0.2f);
		player.setGameMode(GameMode.SURVIVAL);
		VersionIndependentUtils.get().setCustomSpectator(player, true, Bukkit.getOnlinePlayers().stream().filter(pl -> !CustomSpectatorManager.isSpectator(pl)).collect(Collectors.toList()));
		lastDamager.put(player, null);
	}

	public void setEliminatedSpectator(Player player) {
		setSpectator(player);
		player.getInventory().setItem(0, TELEPORT_COMPASS);
	}

	public BaseData getTrappingBase(Player player) {
		if (CooldownManager.get().getTimeLeft(player, Trap.COOLDOWN_ID) <= 0) {
			if (!CustomSpectatorManager.isSpectator(player)) {
				for (BaseData baseData : getBases()) {
					if (baseData.getBedLocation().distance(player.getLocation()) <= 7) {
						if (!baseData.getTraps().isEmpty()) {
							if (!baseData.getOwner().equals(TeamManager.getTeamManager().getPlayerTeam(player))) {
								return baseData;
							}
						}
					}
				}
			}
		}
		return null;
	}

	public void trap(Player player) {
		if (getTrappingBase(player) != null) {
			BaseData baseData = getTrappingBase(player);
			Trap trap = baseData.getTraps().remove(0);
			trap.execute(player, baseData.getOwner());
		}
	}

	public void addWoodSword(Player player) {
		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		BaseData data = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
		if (InventoryUtils.slotsWith(player.getInventory(), Material.DIAMOND_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion()).isEmpty()) {
			if (!InventoryUtils.hasOnCursor(player, Material.DIAMOND_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion())) {
				ItemStack woodSword = new ItemStack(VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion());
				if (data.hasSharpness()) {
					woodSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
				}
				player.getInventory().addItem(woodSword);
			}
		}
	}

	public void updateEnchantment(Player player) {
		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		BaseData data = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
		if (data.getProtectionLevel() > 0) {
			if (player.getInventory().getHelmet() != null) {
				player.getInventory().getHelmet().removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
				player.getInventory().getHelmet().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, data.getProtectionLevel());
			}
			if (player.getInventory().getChestplate() != null) {
				player.getInventory().getChestplate().removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
				player.getInventory().getChestplate().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, data.getProtectionLevel());
			}
			if (player.getInventory().getLeggings() != null) {
				player.getInventory().getLeggings().removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
				player.getInventory().getLeggings().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, data.getProtectionLevel());
			}
			if (player.getInventory().getBoots() != null) {
				player.getInventory().getBoots().removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
				player.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, data.getProtectionLevel());
			}
		}
		if (data.hasSharpness()) {
			InventoryUtils.slotsWith(player.getInventory(), Material.DIAMOND_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, VersionIndependentMaterial.GOLDEN_SWORD.toBukkitVersion(), VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion()).forEach(slot -> player.getInventory().getItem(slot).addEnchantment(Enchantment.DAMAGE_ALL, 1));
		}
	}

	public void handlePlayerRespawnOrJoin(Player player) {
		setSpectator(player);
		if (!players.contains(player.getUniqueId())) {
			players.add(player.getUniqueId());
		}
		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		if (team != null) {
			BaseData base = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
			if (base != null) {
				if (base.hasBed()) {
					if (players.contains(player.getUniqueId())) {
						tryCancelRespawn(player);
						final int[] timePassed = { RESPAWN_TIME_SECONDS };
						BukkitTask task = new BukkitRunnable() {
							@Override
							public void run() {
								if (timePassed[0] >= 1) {
									VersionIndependentUtils.get().sendTitle(player, "", ChatColor.AQUA + "Respawning in " + timePassed[0] + " seconds", 0, 60, 20);
									timePassed[0]--;

								} else {
									Log.debug("Bedwars", "Respawn timer over for " + player.getName());
									if (player.isOnline()) {
										VersionIndependentUtils.get().sendTitle(player, ChatColor.GREEN + "" + ChatColor.BOLD + "RESPAWNED", "", 0, 60, 20);
										tpToBase(player);
									}
									cancel();
								}
							}
						}.runTaskTimer(getPlugin(), 0, 20L);
						respawnTasks.put(player.getUniqueId(), task);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (e.getCause() == TeleportCause.ENDER_PEARL) {
			VersionIndependentSound.ENDERMAN_TELEPORT.playAtLocation(e.getTo(), 2L, 1L);
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if (e.getView().getTopInventory().getType().name().contains("CHEST")) {
			if (e.getOldCursor().getType() == VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion()) {
				e.setCancelled(true);
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getSlotType() != null) {
			if (e.getSlotType() == SlotType.ARMOR) {
				e.setCancelled(true);
				return;
			}
		}

		if (e.getCurrentItem() != null) {
			if (e.getCurrentItem().getType().toString().contains("AXE")) { // Pickaxe also included in this
				if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					e.setCancelled(true);
				}
				if (e.getClickedInventory() != null) {
					if (!(e.getClickedInventory() instanceof PlayerInventory)) {
						e.setCancelled(true);
					}
				}
			}

			if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				if (e.getCurrentItem().getType() == VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion()) {
					e.setCancelled(true);
					return;
				}
			}
			if (e.getClickedInventory().getType().name().contains("CHEST")) {
				Material toCheck = e.getCurrentItem().getType();
				if (e.getHotbarButton() != -1) {
					if (e.getWhoClicked().getInventory().getItem(e.getHotbarButton()) != null) {
						toCheck = e.getWhoClicked().getInventory().getItem(e.getHotbarButton()).getType();
					}
				} else if (e.getCursor() != null) {
					toCheck = e.getCursor().getType();
				}
				if (toCheck == VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion()) {
					if (e.getAction().name().contains("DROP")) {
						e.setCancelled(true);
						return;
					}
					if (e.getAction().name().contains("PLACE")) {
						e.setCancelled(true);
						return;
					}
					if (e.getAction().name().contains("HOTBAR")) {
						e.setCancelled(true);
						return;
					}
				}
			}
		}
		if (e.getCursor() != null) {
			if (e.getCursor().getType().toString().contains("AXE")) { // Pickaxe also included in this
				if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					e.setCancelled(true);
				}

				if (e.getClickedInventory() != null) {
					if (!(e.getClickedInventory() instanceof PlayerInventory)) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	public boolean handleDamage(Player damaged, Entity entityHitter, double damage, DeathType deathType) {
		if (CustomSpectatorManager.isSpectator(damaged)) {
			return true;
		}

		if (invencibility.get(damaged) != 0) {
			return true;
		}

		if (CountdownTaskManager.getTasks().stream().filter(task -> task.getPlayer().equals(damaged)).findFirst().orElse(null) != null) {
			CountdownTaskManager.remove(damaged);
		}

		if (entityHitter != null) {
			Player hitter;
			Entity mainHitter = null;
			if (entityHitter instanceof Projectile) {
				hitter = (Player) ((Projectile) entityHitter).getShooter();
				mainHitter = entityHitter;
			} else if (entityHitter instanceof TNTPrimed) {
				hitter = (Player) ((TNTPrimed) entityHitter).getSource();
				mainHitter = entityHitter;
			} else if (entityHitter instanceof Player) {
				hitter = (Player) entityHitter;
			} else {
				hitter = null;
				mainHitter = entityHitter;
			}
			if (hitter != null) {
				if (invencibility.get(hitter) != 0) {
					invencibility.put(hitter, 0);
				}
			}

			if (mainHitter == null) {
				setLastDamager(damaged, hitter);
			} else {
				setLastDamager(damaged, mainHitter);
			}
			if (damaged.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				damaged.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
		}
		if (damaged.getHealth() - damage <= 0) {
			// yes, im doing it. cry about it.
			damaged.playEffect(EntityEffect.HURT);
			Bukkit.getPluginManager().callEvent(new PlayerKilledEvent(damaged, lastDamager.get(damaged), deathType));
			return true;
		}
		return false;
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		if (started && !ended) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (started && !ended) {
			if (((CraftEntity) e.getEntity()).getHandle() instanceof BedwarsDragon) {
				if (((Zombie) e.getEntity()).getHealth() - e.getFinalDamage() <= 0) {
					e.setCancelled(true);
					e.getEntity().remove();
					return;
				}
			}
			if (e.getEntity() instanceof Player) {
				if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
					e.setCancelled(true);
				}
				Entity lastDamager;
				if (e instanceof EntityDamageByEntityEvent)
					lastDamager = ((EntityDamageByEntityEvent) e).getDamager();
				else
					lastDamager = this.lastDamager.get((Player) e.getEntity());
				if (e instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) e;

					if (((CraftEntity) edbee.getDamager()).getHandle() instanceof CustomFireball) {
						if (!((CustomFireball) ((CraftEntity) edbee.getDamager()).getHandle()).isAllowDamage()) {
							e.setCancelled(true);
							return;
						}
					}
				}
				e.setCancelled(handleDamage((Player) e.getEntity(), lastDamager, e.getDamage(), VersionIndependentUtils.getInstance().getDeathTypeFromDamage(e, lastDamager)));
			}
		}
	}

	public void deathHandle(Player player, Player killer, DeathType type) {
		String deathMessage;

		if (killer != null) {
			if (killer.getUniqueId().equals(player.getUniqueId())) {
				killer = null;
			}
		}

		if (killer != null) {
			switch (type) {
			case COMBAT_NORMAL:
				deathMessage = "%s" + ChatColor.RED + " was killed by %s.";
				break;
			case FALL_SMALL_COMBAT:
				deathMessage = "%s" + ChatColor.RED + " fell from a high place whilst fighting %s.";
				break;
			case EXPLOSION_COMBAT:
				deathMessage = "%s" + ChatColor.RED + " exploded whilst fighting %s.";
				break;
			case COMBAT_FIREBALL:
				deathMessage = "%s" + ChatColor.RED + " was fireballed to death by %s.";
				break;
			case DROWN_COMBAT:
				deathMessage = "%s" + ChatColor.RED + " drowned while fighting %s" + ChatColor.RED + "... brutal.";
				break;
			case SUFFOCATION_COMBAT:
				deathMessage = "%s" + ChatColor.RED + " got stuck in a wall fighting  %s.";
				break;
			case PROJECTILE_ARROW:
				deathMessage = "%s" + ChatColor.RED + " was shot by %s.";
				break;
			case VOID_COMBAT:
				deathMessage = "%s" + ChatColor.RED + " fell into the void fighting %s.";
				break;
			case FIRE_SOURCE_COMBAT:
			case FIRE_NATURAL_COMBAT:
				deathMessage = "%s" + ChatColor.RED + "burnt to death fighting %s.";
				break;
			case PROJECTILE_OTHER:
				deathMessage = "%s" + ChatColor.RED + " died by a projectile in the face from %s";
				break;
			default:
				deathMessage = "%s" + ChatColor.RED + " died because of %s";
				break;
			}
		} else {
			switch (type) {
			case FALL_BIG:
			case FALL_SMALL:
				deathMessage = "%s" + ChatColor.RED + " fell from a high place.";
				break;
			case EXPLOSION:
				deathMessage = "%s" + ChatColor.RED + " exploded.";
				break;
			case DROWN:
				deathMessage = "%s" + ChatColor.RED + " drowned. Why?";
				break;
			case SUFFOCATION:
				deathMessage = "%s" + ChatColor.RED + " got stuck in a wall.";
				break;
			case VOID:
				deathMessage = "%s" + ChatColor.RED + " fell into the void.";
				break;
			case FIRE_NATURAL:
			case FIRE_SOURCE:
				;
				deathMessage = "%s" + ChatColor.RED + " burnt to death.";
				break;
			default:
				deathMessage = "%s" + ChatColor.RED + " died.";
				break;
			}
		}
		deathMessage = String.format(deathMessage, ChatColor.AQUA + player.getName() + ChatColor.RESET, ChatColor.RESET + "" + ChatColor.AQUA + (killer != null ? killer.getName() : "") + ChatColor.RESET);
		Bukkit.getServer().broadcastMessage(deathMessage);

		int ironCollected = 0;
		int goldCollected = 0;
		int diamondCollected = 0;
		int emeraldCollected = 0;

		if (killer != null) {
			for (ItemStack item : player.getInventory().getContents()) {
				if (item != null) {
					if (item.getType() == Material.EMERALD) {
						emeraldCollected += item.getAmount();
						killer.getInventory().addItem(item);
					} else if (item.getType() == Material.DIAMOND) {
						diamondCollected += item.getAmount();
						killer.getInventory().addItem(item);
					} else if (item.getType() == Material.GOLD_INGOT) {
						goldCollected += item.getAmount();
						killer.getInventory().addItem(item);
					} else if (item.getType() == Material.IRON_INGOT) {
						ironCollected += item.getAmount();
						killer.getInventory().addItem(item);
					}
				}
			}
			if (ironCollected != 0) {
				String message = ChatColor.GREEN + "" + ChatColor.BOLD + "+ " + ChatColor.RESET + ChatColor.GRAY + ironCollected + " iron ingot";
				if (ironCollected >= 2) {
					message += "s";
				}
				killer.sendMessage(message);
			}
			if (goldCollected != 0) {
				String message = ChatColor.GREEN + "" + ChatColor.BOLD + "+ " + ChatColor.RESET + ChatColor.GOLD + goldCollected + " gold ingot";
				if (goldCollected >= 2) {
					message += "s";
				}
				killer.sendMessage(message);
			}
			if (diamondCollected != 0) {
				String message = ChatColor.GREEN + "" + ChatColor.BOLD + "+ " + ChatColor.RESET + ChatColor.AQUA + diamondCollected + " diamond";
				if (diamondCollected >= 2) {
					message += "s";
				}
				killer.sendMessage(message);
			}
			if (emeraldCollected != 0) {
				String message = ChatColor.GREEN + "" + ChatColor.BOLD + "+ " + ChatColor.RESET + ChatColor.GREEN + emeraldCollected + " emerald";
				if (emeraldCollected >= 2) {
					message += "s";
				}
				killer.sendMessage(message);
			}
		} else {
			dropItems(player);
		}
		PlayerUtils.clearPlayerInventory(player);

		if (!(NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) <= 1)) {
			NovaBedwars.getInstance().getGame().getAllPlayersAxeTier().put(player, NovaBedwars.getInstance().getGame().getPlayerAxeTier(player) - 1);
		}
		if (!(NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) <= 1)) {
			NovaBedwars.getInstance().getGame().getAllPlayersPickaxeTier().put(player, NovaBedwars.getInstance().getGame().getPlayerPickaxeTier(player) - 1);
		}

		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		if (team != null) {
			if (players.contains(player.getUniqueId())) {
				BaseData base = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
				if (base != null) {
					if (!base.hasBed()) {
						eliminatePlayer(player, killer, PlayerEliminationReason.DEATH);
						setEliminatedSpectator(player);
					} else {
						handlePlayerRespawnOrJoin(player);
					}
				}
			}
		}
	}

	public EnderDragon spawnEndGameDragon(double speed) {
		BedwarsDragon bwd = new BedwarsDragon(getActiveMap().getSpectatorLocation(), config.getDragonRadius());
		bwd.setSpeed(speed);
		bwd.spawn();
		EnderDragon dragon = (EnderDragon) bwd.getBukkitEntity();
		dragon.setRemoveWhenFarAway(false);
		return dragon;
	}

	public void startEndGame() {
		if (endGameEnabled) {
			Log.warn("Bedwars", "EndGame is already enabled.");
			return;
		}
		endGameEnabled = true;
		for (int i = 0; i < 5; i++) {
			BedwarsDragon bwd = new BedwarsDragon(config.getMapCenter().toBukkitLocation(world), config.getDragonRadius());
			bwd.setSpeed(1);
			bwd.spawn();
			dragons.add(bwd.getBukkitEntity());
		}
	}

	public void killAllDragons() {
		dragons.forEach(entity -> {
			if (!entity.isDead()) {
				entity.remove();
			}
		});
	}

	public void spawnDruggedDragons() {
		dragons.clear();
		for (int i = 0; i < 20; i++) {
			BedwarsDragon bwd = new BedwarsDragon(config.getMapCenter().toBukkitLocation(world), config.getDragonRadius());
			bwd.setSpeed(3);
			bwd.setDamage(6);
			bwd.spawn();
			dragons.add(bwd.getBukkitEntity());
		}
	}

	public boolean allDragonsDead() {
		return !dragons.isEmpty() && dragons.stream().allMatch(Entity::isDead);
	}

	public void dropItems(Player player) {
		int ironCollected = 0;
		int goldCollected = 0;
		int diamondCollected = 0;
		int emeraldCollected = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null) {
				if (item.getType() == Material.EMERALD) {
					emeraldCollected += item.getAmount();
				} else if (item.getType() == Material.DIAMOND) {
					diamondCollected += item.getAmount();
				} else if (item.getType() == Material.GOLD_INGOT) {
					goldCollected += item.getAmount();
				} else if (item.getType() == Material.IRON_INGOT) {
					ironCollected += item.getAmount();
				}
			}
		}
		if (ironCollected != 0) {
			ItemStack iron = new ItemStack(Material.IRON_INGOT);
			int value = (int) Math.floor(ironCollected / 64d) - (ironCollected % 64 == 0 ? 1 : 0);
			for (int i = 0; i <= value; i++) {
				ItemStack clone = iron.clone();
				if (i != value) {
					clone.setAmount(64);
				} else {
					clone.setAmount(ironCollected % 64 == 0 ? 64 : ironCollected % 64);
				}
				player.getWorld().dropItemNaturally(player.getLocation(), clone);
			}
		}
		if (goldCollected != 0) {
			ItemStack gold = new ItemStack(Material.GOLD_INGOT);
			int value = (int) Math.floor(goldCollected / 64d) - (goldCollected % 64 == 0 ? 1 : 0);
			for (int i = 0; i <= value; i++) {
				ItemStack clone = gold.clone();
				if (i != value) {
					clone.setAmount(64);
				} else {
					clone.setAmount(goldCollected % 64 == 0 ? 64 : goldCollected % 64);
				}
				player.getWorld().dropItemNaturally(player.getLocation(), clone);
			}
		}
		if (diamondCollected != 0) {
			ItemStack diamond = new ItemStack(Material.DIAMOND);
			int value = (int) Math.floor(diamondCollected / 64d) - (diamondCollected % 64 == 0 ? 1 : 0);
			for (int i = 0; i <= value; i++) {
				ItemStack clone = diamond.clone();
				if (i != value) {
					clone.setAmount(64);
				} else {
					clone.setAmount(diamondCollected % 64 == 0 ? 64 : diamondCollected % 64);
				}
				player.getWorld().dropItemNaturally(player.getLocation(), clone);
			}
		}
		if (emeraldCollected != 0) {
			ItemStack emerald = new ItemStack(Material.EMERALD);
			int value = (int) Math.floor(emeraldCollected / 64d) - (emeraldCollected % 64 == 0 ? 1 : 0);
			for (int i = 0; i <= value; i++) {
				ItemStack clone = emerald.clone();
				if (i != value) {
					clone.setAmount(64);
				} else {
					clone.setAmount(emeraldCollected % 64 == 0 ? 64 : emeraldCollected % 64);
				}
				player.getWorld().dropItemNaturally(player.getLocation(), clone);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerKilledEvent e) {
		if (!started && ended) {
			return;
		}
		Player killer;
		if (e.getKiller() instanceof Projectile) {
			killer = ((Projectile) e.getKiller()).getShooter() instanceof Player ? (Player) ((Projectile) e.getKiller()).getShooter() : null;
		} else if (e.getKiller() instanceof TNTPrimed) {
			killer = ((TNTPrimed) e.getKiller()).getSource() instanceof Player ? (Player) ((TNTPrimed) e.getKiller()).getSource() : null;
		} else if (e.getKiller() instanceof Player) {
			killer = (Player) e.getKiller();
		} else {
			killer = null;
		}
		deathHandle(e.getPlayer(), killer, e.getDeathType());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (started && !ended) {
			if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
				return;
			}

			if (safeLocations.contains(e.getBlock().getLocation())) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "You cant place blocks here. (important place protection)");
				return;
			}
			if (!allowBreak.contains(e.getBlock().getLocation())) {
				allowBreak.add(e.getBlock().getLocation());
			}
			Vector bottom = new Vector(-(config.getBorderRadius() / 2d) + config.getMapCenter().getX(), 0, -(config.getBorderRadius() / 2d) + config.getMapCenter().getZ());
			Vector top = new Vector((config.getBorderRadius() / 2d) + config.getMapCenter().getX(), 256, (config.getBorderRadius() / 2d) + config.getMapCenter().getZ());

			if (!e.getBlock().getLocation().toVector().isInAABB(bottom, top)) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "You cant place blocks here. (reached border)");
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
		Player player = e.getPlayer();

		if (e.getPlayer().getGameMode() != GameMode.SPECTATOR) {
			if (npcs.stream().anyMatch(npc -> npc.getVillager().getUniqueId().equals(e.getRightClicked().getUniqueId()))) {
				e.setCancelled(true);
			}
			npcs.stream().filter(n -> n.getVillager().getUniqueId().equals(e.getRightClicked().getUniqueId())).findFirst().ifPresent(clickedNPC -> new BukkitRunnable() {
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
			}.runTaskLater(getPlugin(), 1L));
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

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (started && !ended) {
			if (e.getTo().getY() <= 0) {
				if (CustomSpectatorManager.isSpectator(e.getPlayer())) {
					e.getPlayer().teleport(getActiveMap().getSpectatorLocation());
				} else {
					DeathType dt = lastDamager.get(e.getPlayer()) != null && !lastDamager.get(e.getPlayer()).getUniqueId().equals(e.getPlayer().getUniqueId()) ? DeathType.VOID_COMBAT : DeathType.VOID;
					e.getPlayer().playEffect(EntityEffect.HURT);
					Bukkit.getPluginManager().callEvent(new PlayerKilledEvent(e.getPlayer(), lastDamager.get(e.getPlayer()), dt));
				}
				return;
			}
			Vector bottom = new Vector(-(config.getBorderRadius() / 2d) + config.getMapCenter().getX(), 0, -(config.getBorderRadius() / 2d) + config.getMapCenter().getZ());
			Vector top = new Vector((config.getBorderRadius() / 2d) + config.getMapCenter().getX(), 256, (config.getBorderRadius() / 2d) + config.getMapCenter().getZ());
			if (!e.getTo().toVector().isInAABB(bottom, top)) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "Reached world border.");
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if (e.getItemDrop().getItemStack().getType() == VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion() || e.getItemDrop().getItemStack().getType().toString().contains("AXE") || e.getItemDrop().getItemStack().getType() == Material.COMPASS) {
			e.setCancelled(true);
		}

		Material type = e.getItemDrop().getItemStack().getType();
		if (type == Material.STONE_SWORD || type == Material.DIAMOND_SWORD || type == Material.IRON_SWORD) {
			if (InventoryUtils.slotsWith(e.getPlayer().getInventory(), VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion()).isEmpty()) {
				if (InventoryUtils.slotsWith(e.getPlayer().getInventory(), Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.STONE_SWORD).size() == 0) {
					int slot = InventoryUtils.slotsWith(e.getPlayer().getInventory(), Material.DIAMOND_SWORD).isEmpty() ? InventoryUtils.slotsWith(e.getPlayer().getInventory(), Material.IRON_SWORD).isEmpty() ? InventoryUtils.slotsWith(e.getPlayer().getInventory(), Material.STONE_SWORD).isEmpty() ? 0 : InventoryUtils.slotsWith(e.getPlayer().getInventory(), Material.STONE_SWORD).get(0) : InventoryUtils.slotsWith(e.getPlayer().getInventory(), Material.IRON_SWORD).get(0) : InventoryUtils.slotsWith(e.getPlayer().getInventory(), Material.DIAMOND_SWORD).get(0);
					e.getPlayer().getInventory().setItem(slot, new ItemBuilder(VersionIndependentMaterial.WOODEN_SWORD).setUnbreakable(true).build());
					updateEnchantment(e.getPlayer());
				}
			}
		}

	}

	private boolean isBed(Location loc, BaseData base) {
		if (VersionIndependentUtils.get().isBed(loc.getBlock()) && VersionIndependentUtils.get().isBed(base.getBedLocation().getBlock())) {
			if (loc.getBlock().getLocation().equals(base.getBedLocation().getBlock().getLocation())) {
				return true;
			}
			Bed bed = (Bed) loc.getBlock().getState().getData();
			Bed baseBed = (Bed) base.getBedLocation().getBlock().getState().getData();
			if (isSameType(bed, baseBed)) {
				return false;
			}

			Block relative = loc.getBlock().getRelative(bed.getFacing().getOppositeFace());
			return VersionIndependentUtils.get().isBed(relative) && locationEquals(relative, base.getBedLocation().getBlock());
		}
		return false;
	}

	private boolean isSameType(Bed bed, Bed other) {
		return (bed.isHeadOfBed() && other.isHeadOfBed()) || (!bed.isHeadOfBed() && !other.isHeadOfBed());
	}

	private boolean locationEquals(Block block, Block other) {
		return (block.getX() == other.getX()) && (block.getY() == other.getY()) && (block.getZ() == other.getZ());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e) {
		if (started && !ended) {
			Player player = e.getPlayer();
			Block block = e.getBlock();
			if (player.getGameMode() == GameMode.CREATIVE && VersionIndependentUtils.get().isBed(block)) {
				BaseData ownerBase = bases.stream().filter(base -> isBed(block.getLocation(), base)).findFirst().orElse(null);
				if (ownerBase != null) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "You cannot break others beds when in creative.");
				}
			}
			if (player.getGameMode() != GameMode.CREATIVE) {
				boolean allow = false;
				if (VersionIndependentUtils.get().isBed(block)) {
					BaseData ownerBase = bases.stream().filter(base -> isBed(block.getLocation(), base)).findFirst().orElse(null);
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
						VersionIndependentSound.ORB_PICKUP.play(player);
						Bukkit.getServer().broadcastMessage(team.getTeamColor() + "" + ChatColor.BOLD + player.getName() + ChatColor.GOLD + ChatColor.BOLD + " broke " + ownerBase.getOwner().getTeamColor() + ChatColor.BOLD + ownerBase.getOwner().getDisplayName() + ChatColor.GOLD + ChatColor.BOLD + "'s bed");
						ownerBase.getOwner().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Bed Broken> " + team.getTeamColor() + "" + ChatColor.BOLD + team.getDisplayName() + ChatColor.RED + ChatColor.BOLD + " broke your bed");
						ownerBase.setBed(false);
						ownerBase.getOwner().getOnlinePlayers().forEach(p -> {
							VersionIndependentSound.WITHER_DEATH.play(p);
							VersionIndependentUtils.get().sendTitle(p, ChatColor.RED + TextUtils.ICON_WARNING + " Bed destroyed " + TextUtils.ICON_WARNING, ChatColor.RED + "You can no longer respawn", 0, 60, 20);
						});
						for (UUID uuid : ownerBase.getOwner().getMembers()) {
							Player damaged = Bukkit.getPlayer(uuid);
							setLastDamager(damaged, e.getPlayer());
						}
						Bukkit.getServer().getPluginManager().callEvent(new BedDestructionEvent(ownerBase.getOwner(), team, player));

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
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
			Player player = e.getPlayer();
			if (VersionIndependentUtils.get().isInteractEventMainHand(e)) {
				ItemStack item = VersionIndependentUtils.get().getItemInMainHand(player);
				if (item != null && item.getItemMeta() != null) {
					if (item.getType() == Material.FIREBALL) {
						if (!CooldownManager.get().isActive(player, FIREBALL_COOLDOWN_ID) || CooldownManager.get().getTimeLeft(player, FIREBALL_COOLDOWN_ID) <= 0) {
							e.setCancelled(true);

							ItemUtils.removeOneFromHand(player);
							Vector dir = player.getEyeLocation().clone().getDirection().multiply(10);

							CustomFireball cf = new CustomFireball(((CraftWorld) player.getWorld()).getHandle(), ((CraftPlayer) player).getHandle(), dir.getX(), dir.getY(), dir.getZ());
							cf.setPositionRotation(player.getEyeLocation().getX(), player.getEyeLocation().getY(), player.getEyeLocation().getZ(), player.getEyeLocation().getYaw(), player.getEyeLocation().getPitch());
							cf.projectileSource = player;
							((CraftWorld) player.getWorld()).getHandle().addEntity(cf);
							final Fireball fireball = (Fireball) cf.getBukkitEntity();
							fireball.setVelocity(player.getLocation().getDirection().multiply(1.5));
							fireball.setBounce(false);
							fireball.setYield(Bedwars.FIREBALL_YIELD);
							fireball.setIsIncendiary(false);
							fireball.setCustomName(ChatColor.GOLD + "Fireball");
							fireball.setCustomNameVisible(false);
							fireball.setShooter(player);
							CooldownManager.get().set(player, FIREBALL_COOLDOWN_ID, 15);
						} else {

							double timeLeft = Math.round((CooldownManager.get().getTimeLeft(player, FIREBALL_COOLDOWN_ID) / 20d) * 10d) / 10d;

							player.sendMessage(ChatColor.RED + "Wait " + timeLeft + " seconds.");
						}
					} else if (item.getType() == Material.TNT && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						e.setCancelled(true);

						ItemUtils.removeOneFromHand(player);

						BlockFace face = e.getBlockFace();
						Location location = e.getClickedBlock().getLocation().clone().add(face.getModX(), face.getModY(), face.getModZ());
						TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(LocationUtils.centerLocation(location), EntityType.PRIMED_TNT);
						tnt.setYield(Bedwars.TNT_YIELD);
						VersionIndependentUtils.get().setSource(tnt, player);
					} else if (item.getItemMeta().equals(TELEPORT_COMPASS.getItemMeta())) {
						SpectatorHolder holder = new SpectatorHolder(players);
						holder.display(player);
					}
				}
			}
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (VersionIndependentUtils.get().isBed(e.getClickedBlock())) {
					e.setCancelled(!e.getPlayer().isSneaking());
				}
			}
		}
	}

	public void setUpPlayer(Player player) {
		getAllPlayersAxeTier().putIfAbsent(player, 0);
		getAllPlayersArmor().putIfAbsent(player, ArmorType.NO_ARMOR);
		getAllPlayersPickaxeTier().putIfAbsent(player, 0);
	}

	public void updatePlayerItems(Player player) {
		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		if (team == null) {
			return;
		}

		int pickaxeTier = getPlayerPickaxeTier(player);
		int axeTier = getPlayerAxeTier(player);

		ArmorType armorType = getPlayerArmor(player);
		List<Integer> pickaxeSlot = InventoryUtils.slotsWith(player.getInventory(), Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE, Material.STONE_PICKAXE, VersionIndependentMaterial.GOLDEN_PICKAXE.toBukkitVersion(), VersionIndependentMaterial.WOODEN_PICKAXE.toBukkitVersion());
		List<Integer> axeSlot = InventoryUtils.slotsWith(player.getInventory(), Material.DIAMOND_AXE, Material.IRON_AXE, Material.STONE_AXE, VersionIndependentMaterial.GOLDEN_AXE.toBukkitVersion(), VersionIndependentMaterial.WOODEN_AXE.toBukkitVersion());

		if (ShopItem.WOOD_PICKAXE.getItemTier(pickaxeTier) != null) {
			if (pickaxeSlot.size() == 0) {
				player.getInventory().addItem(ShopItem.WOOD_PICKAXE.getItemTier(pickaxeTier).getItemStack());
			} else {
				player.getInventory().setItem(pickaxeSlot.stream().findFirst().get(), ShopItem.WOOD_PICKAXE.getItemTier(pickaxeTier).getItemStack());
			}
		}

		if (ShopItem.WOOD_AXE.getItemTier(axeTier) != null) {
			if (axeSlot.size() == 0) {
				player.getInventory().addItem(ShopItem.WOOD_AXE.getItemTier(axeTier).getItemStack());
			} else {
				player.getInventory().setItem(axeSlot.stream().findFirst().get(), ShopItem.WOOD_AXE.getItemTier(axeTier).getItemStack());
			}
		}

		switch (armorType) {
		case GOLD:
			player.getInventory().setLeggings(new ItemBuilder(Material.GOLD_LEGGINGS).setUnbreakable(true).build());
			player.getInventory().setBoots(new ItemBuilder(Material.GOLD_BOOTS).setUnbreakable(true).build());
			break;
		case CHAINMAIL:
			player.getInventory().setLeggings(new ItemBuilder(Material.CHAINMAIL_LEGGINGS).setUnbreakable(true).build());
			player.getInventory().setBoots(new ItemBuilder(Material.CHAINMAIL_BOOTS).setUnbreakable(true).build());
			break;
		case IRON:
			player.getInventory().setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).setUnbreakable(true).build());
			player.getInventory().setBoots(new ItemBuilder(Material.IRON_BOOTS).setUnbreakable(true).build());
			break;
		case DIAMOND:
			player.getInventory().setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).setUnbreakable(true).build());
			player.getInventory().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).setUnbreakable(true).build());
			break;
		default:
			break;
		}

		removeExcessItems(player);
		addWoodSword(player);
		updateEnchantment(player);
	}

	public void removeExcessItems(Player player) {
		if (!InventoryUtils.slotsWith(player.getInventory(), Material.GLASS_BOTTLE).isEmpty()) {
			InventoryUtils.slotsWith(player.getInventory(), Material.GLASS_BOTTLE).forEach(integer -> {
				player.getInventory().setItem(integer, new ItemStack(Material.AIR));
			});
		}
	}

	public void buyForgeUpgrade(Player player, int tier) {
		Team team = TeamManager.getTeamManager().getPlayerTeam(player);
		if (team == null) {
			return;
		}

		BaseData base = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
		if (base == null) {
			return;
		}

		// Cry about it
		switch (tier) {
		case 1:

		case 2:
			generators.stream().filter(g -> g.isOwnedBy(team)).filter(g -> g.getType() == GeneratorType.IRON).forEach(g -> g.decreaseDefaultTime(1));
			generators.stream().filter(g -> g.isOwnedBy(team)).filter(g -> g.getType() == GeneratorType.GOLD).forEach(g -> g.decreaseDefaultTime(2));
			break;

		case 3:
			generators.stream().filter(g -> g.isOwnedBy(team)).filter(g -> g.getType() == GeneratorType.IRON).forEach(g -> g.decreaseDefaultTime(1));
			generators.stream().filter(g -> g.isOwnedBy(team)).filter(g -> g.getType() == GeneratorType.GOLD).forEach(g -> g.decreaseDefaultTime(2));
			break;

		case 4:
			generators.add(new ItemGenerator(base.getSpawnLocation(), GeneratorType.EMERALD, config.getEmeraldForgeTime(), false, true, team));
			break;

		default:
			Log.warn("Bedwars", "Invalid forge upgrade tier " + tier);
			break;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerEliminated(PlayerEliminatedEvent e) {
		OfflinePlayer player = e.getPlayer();
		tryCancelRespawn(player);
	}

	private List<Location> safeLocations = new ArrayList<>();

	public List<Location> getSafeLocations() {
		return safeLocations;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaced(BlockPlaceEvent e) {
		for (Location loc : safeLocations) {
			if (loc.getBlockX() == e.getBlock().getX() && loc.getBlockY() == e.getBlock().getX() && loc.getBlockZ() == e.getBlock().getX()) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks here");
				return;
			}
		}
	}

	public double pos(double value) {
		return value >= 0 ? value : value * -1;
	}

	public void explode(Explosive explosive, int radius, double knockbackMultiplier, double damageMultiplier, boolean silent, boolean particle, Collection<? extends Entity> allowedUpdate, Collection<Block> allowedBreak, int explosionPower) {
		for (Entity entity : explosive.getWorld().getEntities().stream().filter(entity -> explosive.getLocation().distance(entity.getLocation()) <= radius).collect(Collectors.toList())) {
			if (!entity.getUniqueId().equals(explosive.getUniqueId())) {
				if (allowedUpdate.stream().anyMatch(ent -> ent.getUniqueId().equals(entity.getUniqueId()))) {
					Location loc = entity.getLocation();
					Vector vector = loc.clone().toVector().subtract(explosive.getLocation().toVector());
					double xz = pos(vector.getX()) + pos(vector.getZ());
					double mod = ((radius * 2) - xz) / (radius * 2);

					if (vector.getY() < 0) {
						vector.setY(0);
					}

					if (vector.getX() != 0 || vector.getY() != 0 || vector.getZ() != 0) {
						vector.normalize();
					}

					Entity source;

					if (explosive instanceof Fireball) {
						if (((Fireball) explosive).getShooter() instanceof Entity) {
							source = (Entity) ((Fireball) explosive).getShooter();
						} else {
							source = null;
						}
					} else if (explosive instanceof TNTPrimed) {
						source = ((TNTPrimed) explosive).getSource();
					} else {
						source = null;
					}

					vector.setY(vector.getY() + 0.6);
					if (source == null || !entity.getUniqueId().equals(source.getUniqueId())) {
						vector.multiply(knockbackMultiplier / 4);
					} else {
						vector.multiply(knockbackMultiplier);
					}
					vector.multiply(mod);
					vector.setY(Math.min(vector.getY(), knockbackMultiplier / 4));

					entity.setVelocity(vector);

					if (damageMultiplier > 0) {
						double damageMod = damageMultiplier * mod;
						if (explosive instanceof TNTPrimed) {
							TNTPrimed tnt = (TNTPrimed) explosive;
							if (entity instanceof LivingEntity) {
								LivingEntity living = (LivingEntity) entity;
								if (living.getUniqueId().equals(tnt.getSource().getUniqueId())) {
									if (!handleDamage((Player) living, tnt, 1d, DeathType.EXPLOSION_COMBAT)) {
										living.playEffect(EntityEffect.HURT);
										living.damage(1d, tnt);
									}
								} else {
									if (!handleDamage((Player) living, tnt, damageMod, DeathType.EXPLOSION_COMBAT)) {
										living.playEffect(EntityEffect.HURT);
										living.damage(damageMod, tnt);
									}
								}
							}
						} else if (explosive instanceof Fireball) {

							Fireball fireball = (Fireball) explosive;
							if (((CraftFireball) fireball).getHandle() instanceof CustomFireball) {
								CustomFireball cf = (CustomFireball) ((CraftFireball) fireball).getHandle();
								cf.setAllowDamage(true);
							}

							if (fireball.getShooter() instanceof Entity) {
								Entity shooter = (Entity) fireball.getShooter();
								if (entity instanceof LivingEntity) {
									LivingEntity living = (LivingEntity) entity;
									if (living.getUniqueId().equals(shooter.getUniqueId())) {
										if (!handleDamage((Player) living, fireball, 1d, DeathType.EXPLOSION_COMBAT)) {

											living.playEffect(EntityEffect.HURT);
											living.damage(1d, fireball);
										}

									} else {
										if (!handleDamage((Player) living, fireball, damageMod, DeathType.EXPLOSION_COMBAT)) {
											living.playEffect(EntityEffect.HURT);
											living.damage(damageMod, fireball);
										}
									}
								}
							}

						}

					}

				}
			}
		}
		explosive.remove();

		if (!silent) {
			explosive.getWorld().playSound(explosive.getLocation(), Sound.EXPLODE, 4.0f, RandomGenerator.generateFloat(0.56f, 0.84f));
		}
		if (particle) {
			NovaParticleEffect.EXPLOSION_HUGE.display(explosive.getLocation().clone().add(0, 1, 0));
		}

		if (allowedBreak != null) {
			for (Block block : allowedBreak) {
				if (explosive.getLocation().distance(block.getLocation()) <= 5) {
					if (explosive.getLocation().distance(block.getLocation()) >= 3) {
						double value = Math.random();
						if (value >= 0.6 - (0.15 * (explosive.getLocation().distance(block.getLocation())) - 2)) {
							boolean doTheThing = false;
							if (explosionPower >= VersionIndependentUtils.getInstance().getBlockBlastResistance(block)) {
								double mod = (explosionPower - VersionIndependentUtils.getInstance().getBlockBlastResistance(block)) + 1;
								double random = Math.random();
								if (0.75 * mod >= random) {
									doTheThing = true;
								}
							}
							if (doTheThing) {
								boolean drop = Math.random() < 0.5;
								if (drop) {
									block.breakNaturally();
								} else {
									block.setType(Material.AIR);
								}
							}
						}
					} else {
						boolean doTheThing = false;
						if (explosionPower >= VersionIndependentUtils.getInstance().getBlockBlastResistance(block)) {
							double mod = (explosionPower - VersionIndependentUtils.getInstance().getBlockBlastResistance(block)) + 1;
							double random = Math.random();
							if (0.75 * mod >= random) {
								doTheThing = true;
							}
						}
						if (doTheThing) {
							boolean drop = Math.random() < 0.5;
							if (drop) {
								block.breakNaturally();
							} else {
								block.setType(Material.AIR);
							}
						}
					}
				}
			}
		} else {
			for (Block block : generateSphere(explosive.getLocation(), radius)) {
				if (explosive.getLocation().distance(block.getLocation()) >= 3) {
					double value = Math.random();
					if (value >= 0.6 - (0.15 * (explosive.getLocation().distance(block.getLocation())) - 2)) {
						boolean doTheThing = false;
						if (explosionPower >= VersionIndependentUtils.getInstance().getBlockBlastResistance(block)) {
							double mod = (explosionPower - VersionIndependentUtils.getInstance().getBlockBlastResistance(block)) + 1;
							double random = Math.random();
							if (0.75 * mod >= random) {
								doTheThing = true;
							}
						}
						if (doTheThing) {
							boolean drop = Math.random() < 0.5;
							if (drop) {
								block.breakNaturally();
							} else {
								block.setType(Material.AIR);
							}
						}
					}
				} else {
					boolean doTheThing = false;
					if (explosionPower >= VersionIndependentUtils.getInstance().getBlockBlastResistance(block)) {
						double mod = (explosionPower - VersionIndependentUtils.getInstance().getBlockBlastResistance(block)) + 1;
						double random = Math.random();
						if (0.75 * mod >= random) {
							doTheThing = true;
						}
					}
					if (doTheThing) {
						boolean drop = Math.random() < 0.5;
						if (drop) {
							block.breakNaturally();
						} else {
							block.setType(Material.AIR);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent e) {
		if (started && !ended) {
			e.setCancelled(true);
			Player whoDealtIt;
			if (e.getEntityType() == EntityType.FIREBALL) {
				whoDealtIt = (Player) ((Fireball) e.getEntity()).getShooter();
			} else if (e.getEntityType() == EntityType.PRIMED_TNT) {
				whoDealtIt = (Player) ((TNTPrimed) e.getEntity()).getSource();
			} else {
				whoDealtIt = null;
			}
			List<UUID> allowed = players.stream().filter(uuid -> Bukkit.getOfflinePlayer(uuid).isOnline() && !CustomSpectatorManager.isSpectator(Bukkit.getPlayer(uuid)) && Bukkit.getPlayer(uuid).getGameMode() != GameMode.CREATIVE).collect(Collectors.toList());
			List<Player> playersAllowed = new ArrayList<>();
			allowed.forEach(uuid -> {
				if (uuid.equals(whoDealtIt.getUniqueId()) || !TeamManager.getTeamManager().isInSameTeam(uuid, whoDealtIt.getUniqueId())) {
					playersAllowed.add(Bukkit.getPlayer(uuid));
				}
			});
			List<Block> blocks = new ArrayList<>();
			for (Location loc : allowBreak)
				blocks.add(loc.getBlock());

			if (e.getEntityType() == EntityType.FIREBALL) {
				explode((Fireball) e.getEntity(), 4, 4, 3, false, true, playersAllowed, blocks, 2);
			} else if (e.getEntityType() == EntityType.PRIMED_TNT) {
				explode((TNTPrimed) e.getEntity(), 5, 4, 5, false, true, playersAllowed, blocks, 4);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent e) {
		if (started && !ended) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockExplode(BlockExplodeEvent e) {
		if (started && !ended) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		dropItems(e.getPlayer());
		tryCancelRespawn(e.getPlayer());
		CustomSpectatorManager.setSpectator(e.getPlayer(), false);
		PlayerUtils.clearPlayerInventory(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		setUpPlayer(player);

		if (started && !ended) {
			Team team = TeamManager.getTeamManager().getPlayerTeam(e.getPlayer());
			BaseData base = bases.stream().filter(b -> b.getOwner().equals(team)).findFirst().orElse(null);
			if (team == null || base == null || !base.hasBed() || eliminatedPlayers.contains(player.getUniqueId())) {
				setEliminatedSpectator(e.getPlayer());
			} else {
				handlePlayerRespawnOrJoin(player);
			}
			if (!haveWeClearedThisMF.contains(player.getUniqueId())) {
				player.getEnderChest().clear();
				haveWeClearedThisMF.add(player.getUniqueId());
			}
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBuyAttempt(AttemptItemBuyEvent e) {
		Player player = e.getPlayer();
		if (!e.boughtItem()) {
			if (e.getReason() == Reason.NOT_ENOUGHT_MATERIALS) {
				if (!e.isDisableBuiltInMessage()) {
					VersionIndependentSound.ITEM_BREAK.play(player);
					player.sendMessage(ChatColor.RED + "You cant afford that");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		Player player = e.getPlayer();
		if (e.getItem().hasMetadata(ItemGenerator.MULTIPICKUP_METADATA_KEY)) {
			player.getWorld().getNearbyEntities(player.getLocation(), ItemGenerator.MULTI_PICKUP_RANGE, ItemGenerator.MULTI_PICKUP_RANGE, ItemGenerator.MULTI_PICKUP_RANGE).stream().filter(entity -> entity.getType() == EntityType.PLAYER).forEach(entity -> {
				Player player2 = (Player) entity;
				if (player2.equals(player)) {
					return;
				}
				player2.getInventory().addItem(e.getItem().getItemStack());
			});
		}
	}

	@EventHandler
	public void onRainStart(WeatherChangeEvent e) {
		if (e.toWeatherState()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		if (!InventoryUtils.slotsWith(e.getPlayer().getInventory(), VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion()).isEmpty()) {
			int first = InventoryUtils.slotsWith(e.getPlayer().getInventory(), VersionIndependentMaterial.WOODEN_SWORD.toBukkitVersion()).get(0);
			Material type = e.getItem().getItemStack().getType();
			if (type == Material.STONE_SWORD || type == Material.IRON_SWORD || type == Material.DIAMOND_SWORD) {
				ItemStack toSet = e.getItem().getItemStack().clone();
				ItemStack toRemove = e.getItem().getItemStack();
				ItemMeta meta = toRemove.getItemMeta();
				meta.setDisplayName("REMOVE");
				toRemove.setItemMeta(meta);
				e.getPlayer().getInventory().setItem(first, toSet);

				Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
					toRemove.setAmount(1);
					InventoryUtils.removeItem(e.getPlayer().getInventory(), toRemove);
					updateEnchantment(e.getPlayer());
				}, 1L);

			}
		}
	}

	@EventHandler
	public void onEliminate(PlayerEliminatedEvent e) {
		eliminatedPlayers.add(e.getPlayer().getUniqueId());
	}

	public void setLastDamager(Player player, Entity damager) {
		lastDamager.putIfAbsent(player, damager);
		lastDamager.put(player, damager);
		CountdownTaskManager.addTask(new CountdownTask(() -> {
			lastDamager.putIfAbsent(player, null);
			lastDamager.put(player, null);
			CountdownTaskManager.remove(player);
		}, KILL_CREDIT_TIMER_SECONDS * 20, player));
	}

	public ArrayList<Block> generateSphere(final Location center, final int radius) {
		ArrayList<Block> sphere = new ArrayList<>();
		for (int Y = -radius; Y < radius; Y++)
			for (int X = -radius; X < radius; X++)
				for (int Z = -radius; Z < radius; Z++)
					if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
						final Block block = center.getWorld().getBlockAt(X + center.getBlockX(), Y + center.getBlockY(), Z + center.getBlockZ());
						sphere.add(block);
					}
		return sphere;
	}
}