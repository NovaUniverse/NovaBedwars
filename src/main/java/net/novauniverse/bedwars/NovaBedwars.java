package net.novauniverse.bedwars;

import net.novauniverse.bedwars.game.Bedwars;
import net.novauniverse.bedwars.game.commands.ImportBedwarsPreferences;
import net.novauniverse.bedwars.game.config.BedwarsConfig;
import net.novauniverse.bedwars.game.debug.CommandFromMessage;
import net.novauniverse.bedwars.game.debug.GivePotion;
import net.novauniverse.bedwars.game.debug.HashMapDebugger;
import net.novauniverse.bedwars.game.debug.MissileWarsDebugCommands;
import net.novauniverse.bedwars.game.debug.ShopItemMetasDebugger;
import net.novauniverse.bedwars.game.debug.ShopOpenDebug;
import net.novauniverse.bedwars.game.debug.UUIDGetter;
import net.novauniverse.bedwars.game.debug.UpgradeShopOpenDebug;
import net.novauniverse.bedwars.game.enums.ArmorType;
import net.novauniverse.bedwars.utils.HypixelAPI;
import net.novauniverse.bedwars.utils.preferences.api.PreferenceAPI;
import net.novauniverse.bedwars.utils.preferences.api.PreferenceAPISettings;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.manager.CustomSpectatorManager;
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
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public final class NovaBedwars extends JavaPlugin implements Listener {
	private static NovaBedwars instance;

	private Bedwars game;
	private boolean disableDefaultEndSound;

	private HypixelAPI hypixelAPI;
	private PreferenceAPI preferenceAPI;

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
			mapFolder = new File(NovaCoreGameEngine.getInstance().getRequestedGameDataDirectory().getAbsolutePath() + File.separator + getName() + File.separator + "Maps");
			worldFolder = new File(NovaCoreGameEngine.getInstance().getRequestedGameDataDirectory().getAbsolutePath() + File.separator + getName() + File.separator + "Worlds");
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

		MissileWarsDebugCommands.register();
		HashMapDebugger.register();
		ShopItemMetasDebugger.register();
		UUIDGetter.register();
		GivePotion.register();
		CommandFromMessage.register();
		ShopOpenDebug.register();
		UpgradeShopOpenDebug.register();
		CommandRegistry.registerCommand(new ImportBedwarsPreferences());

		Bukkit.getOnlinePlayers().forEach(player -> {
			getGame().getAllPlayersPickaxeTier().putIfAbsent(player, 0);
			getGame().getAllPlayersAxeTier().putIfAbsent(player, 0);
			getGame().getAllPlayersArmor().putIfAbsent(player, ArmorType.NO_ARMOR);
		});

		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public String getName() {
				return "damageTest";
			}

			@Override
			public String getPermission() {
				return "damageTest";
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
				((Player) commandSender).damage(2);
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
				Bukkit.getScheduler().runTaskLater(instance, () -> game.explode(tnt, 4, 4, 5, false, true, Bukkit.getOnlinePlayers(), null), 40L);
			}
		});
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);
	}
}

// UwU, Daddy
