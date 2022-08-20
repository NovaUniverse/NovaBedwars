package net.novauniverse.bedwars.game.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

import net.novauniverse.bedwars.NovaBedwars;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.VectorUtils;

public class ItemGenerator {
	public static final String NO_MERGE_METADATA_KEY = "novabedwarsnomerge";
	public static final String MULTIPICKUP_METADATA_KEY = "novabedwarsmultipickup";
	
	public static final double HOLOGRAM_OFFSET = 2.5D;
	
	public static final double MULTI_PICKUP_RANGE = 1.25D;

	private Location location;

	private GeneratorType type;

	private int generateItemDelay;

	private boolean full;

	private int timeLeft;

	private Team owner;

	private Hologram hologram;

	private boolean multiPickup;

	public ItemGenerator(Location location, GeneratorType type, int generateItemDelay, boolean useHologram, boolean multiPickup) {
		this(location, type, generateItemDelay, useHologram, multiPickup, null);
	}

	public ItemGenerator(Location location, GeneratorType type, int generateItemDelay, boolean useHologram, boolean multiPickup, Team owner) {
		this.location = location;
		this.type = type;
		this.generateItemDelay = generateItemDelay;
		this.multiPickup = multiPickup;
		this.owner = owner;

		this.full = false;

		hologram = null;
		if (useHologram) {
			hologram = HologramsAPI.createHologram(NovaBedwars.getInstance(), location.clone().add(0D, ItemGenerator.HOLOGRAM_OFFSET, 0D));
			hologram.appendTextLine(type.getColor() + "" + ChatColor.BOLD + type.getName() + " generator");
			hologram.appendTextLine(ChatColor.AQUA + "--:--");
		}

		this.timeLeft = 1;
	}

	public void countdown() {
		List<Entity> entities = location.getWorld().getNearbyEntities(location, 2.0D, 2.0D, 2.0D).stream().filter(e -> e.getType() == EntityType.DROPPED_ITEM).collect(Collectors.toList());
		int count = 0;
		for (Entity e : entities) {
			count += ((Item) e).getItemStack().getAmount();
		}
		timeLeft--;
		if (timeLeft <= 0) {
			timeLeft = generateItemDelay;
			if (count < type.getMaxItems()) {
				dropItem();
			}
		}

		if (hologram != null) {
			TextLine line = (TextLine) hologram.getLine(1);
			if (count < type.getMaxItems()) {
				line.setText(ChatColor.AQUA + TextUtils.secondsToTime(timeLeft));
			} else {
				line.setText(ChatColor.RED + TextUtils.ICON_WARNING + " Generator full " + TextUtils.ICON_WARNING);
			}
		}

	}

	public void dropItem() {
		ItemStack itemToDrop = new ItemBuilder(type.getMaterial()).setAmount(1).build();
		Item item = location.getWorld().dropItem(location.clone().add(0D, 1D, 0D), itemToDrop);
		item.setMetadata(ItemGenerator.NO_MERGE_METADATA_KEY, new FixedMetadataValue(NovaBedwars.getInstance(), true));
		if(multiPickup) {
			item.setMetadata(ItemGenerator.MULTIPICKUP_METADATA_KEY, new FixedMetadataValue(NovaBedwars.getInstance(), true));
		}
		item.setVelocity(VectorUtils.getEmptyVector());
	}

	public Team getOwner() {
		return owner;
	}

	public boolean isFull() {
		return full;
	}

	public boolean isMultiPickup() {
		return multiPickup;
	}

	public Location getLocation() {
		return location;
	}

	public int getTimeLeft() {
		return timeLeft;
	}

	public GeneratorType getType() {
		return type;
	}

	public int getGenerateItemDelay() {
		return generateItemDelay;
	}

	public void setGenerateItemDelay(int generateItemDelay) {
		if (generateItemDelay <= 0) {
			generateItemDelay = 1;
		}

		if (this.timeLeft > generateItemDelay) {
			this.timeLeft = generateItemDelay;
		}

		this.generateItemDelay = generateItemDelay;
	}

	public void decreaseDefaultTime(int speedIncrement) {
		this.setGenerateItemDelay(this.getGenerateItemDelay() - speedIncrement);
	}

	public boolean isOwnedBy(Team team) {
		if (owner != null) {
			return owner.equals(team);
		}
		return false;
	}
}