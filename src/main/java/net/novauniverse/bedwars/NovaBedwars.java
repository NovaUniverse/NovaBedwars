package net.novauniverse.bedwars;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.novauniverse.bedwars.game.Bedwars;
import net.novauniverse.bedwars.game.commands.ImportBedwarsPreferences;
import net.novauniverse.bedwars.game.config.BedwarsConfig;
import net.novauniverse.bedwars.game.debug.BedWarsDebugCommands;
import net.novauniverse.bedwars.game.debug.CommandFromMessage;
import net.novauniverse.bedwars.game.debug.GivePotion;
import net.novauniverse.bedwars.game.debug.HashMapDebugger;
import net.novauniverse.bedwars.game.debug.ShopItemMetasDebugger;
import net.novauniverse.bedwars.game.debug.ShopOpenDebug;
import net.novauniverse.bedwars.game.debug.UUIDGetter;
import net.novauniverse.bedwars.game.debug.UpgradeShopOpenDebug;
import net.novauniverse.bedwars.game.entity.dragon.BedwarsDragon;
import net.novauniverse.bedwars.utils.HypixelAPI;
import net.novauniverse.bedwars.utils.preferences.api.PreferenceAPI;
import net.novauniverse.bedwars.utils.preferences.api.PreferenceAPISettings;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.commons.utils.ReflectUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;
import net.zeeraa.novacore.spigot.gameengine.NovaCoreGameEngine;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.map.mapmodule.MapModuleManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.mapselector.selectors.RandomMapSelector;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.mapselector.selectors.guivoteselector.GUIMapVote;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.cooldown.CooldownManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class NovaBedwars extends JavaPlugin implements Listener {
	private static NovaBedwars instance;

	private Bedwars game;
	private boolean disableDefaultEndSound;

	private HypixelAPI hypixelAPI;
	private PreferenceAPI preferenceAPI;

	// Map<inventory_location, list_location>
	public static final Map<Integer, Integer> locationsMap = new HashMap<>();

	static {
		locationsMap.put(1,0);
		locationsMap.put(2,1);
		locationsMap.put(3,2);
		locationsMap.put(4,3);
		locationsMap.put(5,4);
		locationsMap.put(6,5);
		locationsMap.put(7,6);
		locationsMap.put(10,7);
		locationsMap.put(11,8);
		locationsMap.put(12,9);
		locationsMap.put(13,10);
		locationsMap.put(14,11);
		locationsMap.put(15,12);
		locationsMap.put(16,13);
		locationsMap.put(19,14);
		locationsMap.put(20,15);
		locationsMap.put(21,16);
		locationsMap.put(22,17);
		locationsMap.put(23,18);
		locationsMap.put(24,19);
		locationsMap.put(25,20);
	}

	public static NovaBedwars getInstance() {
		return instance;
	}

	public boolean isDisableDefaultEndSound() {
		return disableDefaultEndSound;
	}

	public void setDisableDefaultEndSound(boolean disableDefaultEndSound) {
		this.disableDefaultEndSound = disableDefaultEndSound;
	}

	public Bedwars getGame() {
		return game;
	}

	@Nullable
	public HypixelAPI getHypixelAPI() {
		return hypixelAPI;
	}

	public boolean hasHypixelAPI() {
		return hypixelAPI != null;
	}

	public PreferenceAPI getPreferenceAPI() {
		return preferenceAPI;
	}

	public boolean hasPreferenceAPI() {
		return preferenceAPI != null;
	}
	@Override
	public void onEnable() {
		NovaBedwars.instance = this;
		saveDefaultConfig();

		hypixelAPI = null;
		preferenceAPI = null;

		disableDefaultEndSound = getConfig().getBoolean("disable_default_end_sound");
		boolean combatTagging = getConfig().getBoolean("combat_tagging");
		boolean disableNovaCoreGameLobby = getConfig().getBoolean("disable_novacore_gamelobby");

		String hypixelAPIKey = getConfig().getString("hypixel_api_key");
		if (hypixelAPIKey.length() > 0) {
			hypixelAPI = new HypixelAPI(hypixelAPIKey);
		}

		boolean preferenceApiEnabled = getConfig().getBoolean("preference_api_enabled");
		String preferenceApiUrl = getConfig().getString("preference_api_url");
		String preferenceApiKey = getConfig().getString("preference_api_key");

		PreferenceAPISettings apiConfig = new PreferenceAPISettings(preferenceApiEnabled, preferenceApiUrl, preferenceApiKey);

		if (apiConfig.isEnabled()) {
			preferenceAPI = new PreferenceAPI(apiConfig);
		}

		File mapFolder = new File(this.getDataFolder().getPath() + File.separator + "Maps");
		File worldFolder = new File(this.getDataFolder().getPath() + File.separator + "Worlds");

		if (NovaCoreGameEngine.getInstance().getRequestedGameDataDirectory() != null) {
			mapFolder = new File(NovaCoreGameEngine.getInstance().getRequestedGameDataDirectory().getAbsolutePath() + File.separator + "Bedwars" + File.separator + "Maps");
			worldFolder = new File(NovaCoreGameEngine.getInstance().getRequestedGameDataDirectory().getAbsolutePath() + File.separator + "Bedwars" + File.separator + "Worlds");
		}

		File mapOverrides = new File(this.getDataFolder().getPath() + File.separator + "map_overrides.json");
		if (mapOverrides.exists()) {
			Log.info(getName(), "Trying to read map overrides file");
			try {
				JSONObject mapFiles = JSONFileUtils.readJSONObjectFromFile(mapOverrides);

				boolean relative = mapFiles.getBoolean("relative");

				mapFolder = new File((relative ? this.getDataFolder().getPath() + File.separator : "") + mapFiles.getString("maps_folder"));
				worldFolder = new File((relative ? this.getDataFolder().getPath() + File.separator : "") + mapFiles.getString("worlds_folder"));

				Log.info(getName(), "New paths:");
				Log.info(getName(), "Map folder: " + mapFolder.getAbsolutePath());
				Log.info(getName(), "World folder: " + worldFolder.getAbsolutePath());
			} catch (JSONException | IOException e) {
				e.printStackTrace();
				Log.error(getName(), "Failed to read map overrides from file " + mapOverrides.getAbsolutePath());
			}
		}

		try {
			FileUtils.forceMkdir(getDataFolder());
			FileUtils.forceMkdir(mapFolder);
			FileUtils.forceMkdir(worldFolder);
		} catch (IOException e1) {
			e1.printStackTrace();
			Log.fatal(getName(), "Failed to setup data directory");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		ModuleManager.scanForModules(this, "net.novauniverse.bedwars.game.modules");

		ModuleManager.enable(GameManager.class);
		if (!disableNovaCoreGameLobby) {
			ModuleManager.enable(GameLobby.class);
		}

		ModuleManager.enable(CooldownManager.class);

		MapModuleManager.addMapModule("bedwars.config", BedwarsConfig.class);

		GameManager.getInstance().setUseCombatTagging(combatTagging);

		Bukkit.getPluginManager().registerEvents(this, this);

		this.game = new Bedwars();

		GameManager.getInstance().loadGame(game);

		if (!disableNovaCoreGameLobby) {
			GUIMapVote mapSelector = new GUIMapVote();
			GameManager.getInstance().setMapSelector(mapSelector);
			Bukkit.getServer().getPluginManager().registerEvents(mapSelector, this);
		} else {
			GameManager.getInstance().setMapSelector(new RandomMapSelector());
		}

		Log.info(getName(), "Loading maps from " + mapFolder.getPath());
		GameManager.getInstance().readMapsFromFolderDelayed(mapFolder, worldFolder);

		BedWarsDebugCommands.register();
		HashMapDebugger.register();
		ShopItemMetasDebugger.register();
		UUIDGetter.register();
		GivePotion.register();
		CommandFromMessage.register();
		ShopOpenDebug.register();
		UpgradeShopOpenDebug.register();
		CommandRegistry.registerCommand(new ImportBedwarsPreferences());

		Bukkit.getOnlinePlayers().forEach(player -> getGame().setUpPlayer(player));

		VersionIndependentUtils.get().registerCustomEntityWithEntityId(BedwarsDragon.class, "bedwars_dragon", 63);
		registerDebugs();

	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);
	}

	public void registerDebugs() {

		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public String getName() {
				return "spawnlookingentity";
			}

			@Override
			public String getPermission() {
				return "spawnlookingentity";
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
				Player player = (Player) commandSender;
				Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
				VersionIndependentUtils.get().setAI(villager, false);
				new BukkitRunnable() {
					@Override
					public void run() {
						lookAtPlayer(villager);
					}
				}.runTaskTimer(NovaBedwars.this, 0,1);
			}
			public void lookAtPlayer(LivingEntity entity) {
				List<Entity> entities = entity.getWorld().getNearbyEntities(entity.getLocation(), 3, 3, 3).stream().filter(e -> e.getType() == EntityType.PLAYER).collect(Collectors.toList());
				Entity closest = getClosest(entities, entity.getLocation());
				if (closest != null) {
					Player player = (Player) closest;
					Vector vec = player.getEyeLocation().toVector().subtract(entity.getEyeLocation().toVector()).normalize();
					Location loc = entity.getEyeLocation().clone();
					loc.setDirection(vec);
					loc.setX(entity.getLocation().getX());
					loc.setY(entity.getLocation().getY());
					loc.setZ(entity.getLocation().getZ());
					entity.teleport(loc);
				}

			}

			private Entity getClosest(List<Entity> list, Location toGo) {
				final Entity[] closest = {null};
				list.forEach(entity -> {
					if (closest[0] == null) {
						closest[0] = entity;
					} else {
						if (toGo.distanceSquared(entity.getLocation()) < toGo.distanceSquared(closest[0].getLocation())) {
							closest[0] = entity;
						}
					}
				});
				return closest[0];
			}
		});

		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public String getName() {
				return "DragonTest";
			}

			@Override
			public String getPermission() {
				return "bedwars_lmao";
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
				Player player = (Player) commandSender;
				BedwarsDragon bwd = new BedwarsDragon(player.getLocation(), 50);
				bwd.setSpeed(1);
				bwd.spawn();
			}
		});

		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public String getName() {
				return "explosiontest";
			}

			@Override
			public String getPermission() {
				return "explosiontest";
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
				Player player = (Player) commandSender;
				TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
				VersionIndependentUtils.get().setSource(tnt, player);
				Bukkit.getScheduler().runTaskLater(instance, () -> game.explode(tnt, 4, 4, 5, false, true, Bukkit.getOnlinePlayers(), null, 1), 40L);
			}
		});
		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public String getName() {
				return "checkTargets";
			}

			@Override
			public String getPermission() {
				return "aosoakf";
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
				Player player = (Player) commandSender;
				player.getWorld().getEntities().forEach(entity -> {
					if (((CraftEntity)entity).getHandle() instanceof BedwarsDragon) {
						BedwarsDragon dragon = (BedwarsDragon) ((CraftEntity) entity).getHandle();

						List goalB = (List) ReflectUtils.getPrivateField("b", PathfinderGoalSelector.class, dragon.goalSelector);
						List goalC = (List)ReflectUtils.getPrivateField("c", PathfinderGoalSelector.class, dragon.goalSelector);

						List targetB = (List)ReflectUtils.getPrivateField("b", PathfinderGoalSelector.class, dragon.targetSelector);
						List targetC = (List)ReflectUtils.getPrivateField("c", PathfinderGoalSelector.class, dragon.targetSelector);

						Log.debug("" + goalB);
						Log.debug("" + goalC);
						Log.debug("" + targetB);
						Log.debug("" + targetC);
					}
				});

			}
		});

		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public String getName() {
				return "CheckDragon";
			}

			@Override
			public String getPermission() {
				return "null";
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
				Player player = (Player) commandSender;

				EnderDragon ed = (EnderDragon) player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDER_DRAGON);


				EntityEnderDragon dragon = ((CraftEnderDragon) ed).getHandle();

				List goalB = (List) ReflectUtils.getPrivateField("b", PathfinderGoalSelector.class, dragon.goalSelector);
				List goalC = (List)ReflectUtils.getPrivateField("c", PathfinderGoalSelector.class, dragon.goalSelector);

				List targetB = (List)ReflectUtils.getPrivateField("b", PathfinderGoalSelector.class, dragon.targetSelector);
				List targetC = (List)ReflectUtils.getPrivateField("c", PathfinderGoalSelector.class, dragon.targetSelector);

				Log.debug("" + goalB);
				Log.debug("" + goalC);
				Log.debug("" + targetB);
				Log.debug("" + targetC);
			}
		});
		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public String getName() {
				return "boundingboxtest";
			}

			@Override
			public String getPermission() {
				return "boundingboxtest";
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
				Player player = (Player) commandSender;
				EntityPlayer ep = ((CraftPlayer) player).getHandle();
				System.out.println(ep.getBoundingBox().a);
				System.out.println(ep.getBoundingBox().b);
				System.out.println(ep.getBoundingBox().c);
				System.out.println(ep.getBoundingBox().d);
				System.out.println(ep.getBoundingBox().e);
				System.out.println(ep.getBoundingBox().f);
			}
		});
		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public String getName() {
				return "turntest";
			}

			@Override
			public String getPermission() {
				return "turntest";
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
				EnderDragon ed = (EnderDragon) ((Player) commandSender).getWorld().spawnEntity(((Player) commandSender).getLocation(), EntityType.ENDER_DRAGON);
				Location initialLoc = ed.getLocation().clone();
				float[] fl = new float[]{-690};
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = ed.getLocation().clone();
						loc.setX(initialLoc.getX());
						loc.setY(initialLoc.getY());
						loc.setZ(initialLoc.getZ());

						if (fl[0] == -690) {
							fl[0] = loc.getYaw() + 5f;
						} else {
							fl[0] += 5f;
						}
						loc.setYaw(correct180Degree(fl[0]));
						ed.teleport(loc);
						System.out.println(ed.getLocation().getYaw());
					}
				}.runTaskTimer(NovaBedwars.this, 0, 1);
			}
		});
	}

	public float correct180Degree(float degree) {
		if (degree > 180) {
			return correct180Degree(degree - 360);
		} else if (degree < -180) {
			return correct180Degree(degree + 360);
		} else {
			return degree;
		}
	}

}

// UwU, Daddy
