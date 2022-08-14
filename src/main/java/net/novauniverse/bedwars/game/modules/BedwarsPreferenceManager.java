package net.novauniverse.bedwars.game.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONArray;
import org.json.JSONException;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.enums.Items;
import net.novauniverse.bedwars.utils.HypixelAPI;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class BedwarsPreferenceManager extends NovaModule implements Listener {
	private static final int DEFAULT_HYPIXEL_COOLDOWN_TIME = 60 * 2; // 2 minutes

	private static BedwarsPreferenceManager instance;

	private Map<UUID, BedwarsPreferences> preferences;
	private List<HypixelAPIRequestCooldown> cooldown;

	private Task cooldownTask;

	public static BedwarsPreferenceManager getInstance() {
		return instance;
	}

	public BedwarsPreferenceManager() {
		super("Bedwars.PreferenceManager");
		BedwarsPreferenceManager.instance = this;

		this.cooldown = new ArrayList<>();
		this.preferences = new HashMap<>();
		this.cooldownTask = new SimpleTask(NovaBedwars.getInstance(), new Runnable() {
			@Override
			public void run() {
				cooldown.forEach(c -> c.decrement());
				cooldown.removeIf(c -> c.hasExpired());
			}
		}, 20L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(cooldownTask);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(cooldownTask);
		preferences.clear();
		cooldown.clear();
	}

	public boolean isHypixelRequestCooldownActive(Player player) {
		return cooldown.stream().filter(c -> c.getName().equalsIgnoreCase(player.getName())).findFirst().isPresent();
	}

	public boolean tryImportHypixelPreferences(final Player player, @Nullable final PreferenceAPIRequestCallback callback) {
		if (!NovaBedwars.getInstance().hasHypixelAPI()) {
			return false;
		}

		cooldown.add(new HypixelAPIRequestCooldown(player.getName(), BedwarsPreferenceManager.DEFAULT_HYPIXEL_COOLDOWN_TIME));

		AsyncManager.runAsync(new Runnable() {
			@Override
			public void run() {
				try {
					final List<String> hypixelData = HypixelAPI.bedwarsPreferencesAsList(NovaBedwars.getInstance().getHypixelAPI().getProfile(player));

					AsyncManager.runSync(new Runnable() {
						@Override
						public void run() {
							final List<Items> items = new ArrayList<>();

							hypixelData.forEach(h -> {
								if (h == "null") {
									items.add(null);
								} else {
									Items item = Arrays.stream(Items.values()).filter(i -> i.getHypixelCounterpart() != null).filter(i -> i.getHypixelCounterpart().equalsIgnoreCase(h)).findFirst().orElse(null);
									if (item == null) {
										Log.trace("BedwarsPreferenceManager", "Could not map hypixel item " + h + " to NovaBedwars item");
										items.add(null);
									} else {
										Log.trace("BedwarsPreferenceManager", "Mapped hypixel item " + h + " to " + item.name());
										items.add(item);
									}
								}

								preferences.put(player.getUniqueId(), new BedwarsPreferences(player.getUniqueId(), items));
								if (callback != null) {
									callback.onResult(true, null);
								}
							});
						}
					});
				} catch (Exception e) {
					Log.warn("BedwarsPreferenceManager", "Failed to fetch/parse hypixel preference data. " + e.getClass().getName() + " " + e.getMessage());
					if (callback != null) {
						AsyncManager.runSync(new Runnable() {
							@Override
							public void run() {
								callback.onResult(false, e);
							}
						});
					}
				}
			}
		});

		return true;
	}

	public boolean savePreferences(final BedwarsPreferences preferences, @Nullable final PreferenceAPIRequestCallback callback) {
		if (!NovaBedwars.getInstance().hasPreferenceAPI()) {
			return false;
		}

		AsyncManager.runAsync(new Runnable() {
			@Override
			public void run() {
				try {
					NovaBedwars.getInstance().getPreferenceAPI().updatePreferences(preferences.getUuid(), preferences.toJSON());
					Log.debug("BedwarsPreferenceManager", "Preferences updated for " + preferences.getUuid());
					if (callback != null) {
						AsyncManager.runSync(new Runnable() {
							@Override
							public void run() {
								callback.onResult(true, null);
							}
						});
					}
				} catch (final IOException e) {
					Log.warn("BedwarsPreferenceManager", "Failed to update preferences for " + preferences.getUuid() + ". " + e.getClass().getName() + " " + e.getMessage());
					if (callback != null) {
						AsyncManager.runSync(new Runnable() {
							@Override
							public void run() {
								callback.onResult(false, e);
							}
						});
					}

				}
			}
		});
		return true;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (NovaBedwars.getInstance().hasPreferenceAPI()) {
			final Player player = e.getPlayer();
			Log.debug("BedwarsPreferenceManager", "Fetching item shop preferences for " + player.getName());
			AsyncManager.runAsync(new Runnable() {

				@Override
				public void run() {
					try {
						final JSONArray json = NovaBedwars.getInstance().getPreferenceAPI().getPreferences(player);
						if (json != null) {
							AsyncManager.runSync(new Runnable() {
								@Override
								public void run() {
									BedwarsPreferences playerPreferences = BedwarsPreferences.fromJSON(player.getUniqueId(), json);
									preferences.put(player.getUniqueId(), playerPreferences);
									Log.debug("BedwarsPreferenceManager", "Preferences fetched for " + player.getName());
								}
							});
						} else {
							Log.debug("BedwarsPreferenceManager", player.getName() + " has no bedwars item shop preferences");
						}
					} catch (JSONException | IOException e) {
						Log.warn("BedwarsPreferenceManager", "Failed to fetch preferences for " + player.getName() + ". " + e.getClass().getName() + " " + e.getMessage());
						player.sendMessage(ChatColor.RED + "Could not sync your item shop preferences");
					}
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		preferences.remove(e.getPlayer().getUniqueId());
	}
}