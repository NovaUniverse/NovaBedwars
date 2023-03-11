package net.novauniverse.bedwars.game.generator;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.VectorUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.stream.Collectors;

public class BaseGenerator {

    private BaseData base;
    private int ironTimeTicks;
    private int ironMod;

    private int ironTick;

    private int goldTimeTicks;
    private int goldMod;
    private int goldTick;

    private int emeraldTimeTicks;
    private int emeraldMod;
    private int emeraldTick;

    private boolean spawnEmeralds;

    public BaseGenerator(BaseData base, int ironTimeTicks, int goldTimeTicks, int emeraldTimeTicks) {

        this.base = base;
        this.ironTimeTicks = ironTimeTicks;
        this.ironMod = 1;
        this.goldTimeTicks = goldTimeTicks;
        this.goldMod = 1;
        this.emeraldTimeTicks = emeraldTimeTicks;
        this.emeraldMod = 1;
        this.spawnEmeralds = false;
    }

    public BaseData getBase() {
        return base;
    }

    public int getIronTimeTicks() {
        return ironTimeTicks;
    }

    public int getGoldTimeTicks() {
        return goldTimeTicks;
    }

    public int getEmeraldTimeTicks() {
        return emeraldTimeTicks;
    }

    public boolean isSpawnEmeralds() {
        return spawnEmeralds;
    }

    public int getIronMod() {
        return ironMod;
    }

    public int getGoldMod() {
        return goldMod;
    }

    public int getEmeraldMod() {
        return emeraldMod;
    }

    public void multiplyIron(int multiple) {
        ironMod = multiple;
    }

    public void multiplyGold(int multiple) {
        goldMod = multiple;
    }

    public void multiplyEmerald(int multiple) {
        emeraldMod = multiple;
    }

    public void dropItem(Material material) {
        ItemStack itemToDrop = new ItemBuilder(material).setAmount(1).build();
        Item item = base.getForgeLocation().getWorld().dropItem(base.getForgeLocation().clone().add(0D, 1D, 0D), itemToDrop);
        item.setMetadata(ItemGenerator.NO_MERGE_METADATA_KEY, new FixedMetadataValue(NovaBedwars.getInstance(), true));
        item.setMetadata(ItemGenerator.MULTIPICKUP_METADATA_KEY, new FixedMetadataValue(NovaBedwars.getInstance(), true));

        item.setVelocity(VectorUtils.getEmptyVector());
    }
    public void tick() {
        multiplyIron(base.getForgeLevel() + 1);
        multiplyGold(base.getForgeLevel() + 1);
        if (base.getForgeLevel() >= 4 && !spawnEmeralds) {
            spawnEmeralds = true;
        }

        ironTick++;
        goldTick++;
        if (spawnEmeralds) {
            emeraldTick++;
        }

        List<Entity> entities = getBase().getForgeLocation().getWorld().getNearbyEntities(getBase().getForgeLocation(), 2,2,2).stream().filter(entity -> entity.getType() == EntityType.DROPPED_ITEM).collect(Collectors.toList());

        int ironAmount = 0;
        int goldAmount = 0;
        int emeraldAmount = 0;
        for (Entity entity : entities) {
            Item item = (Item) entity;
            switch (item.getItemStack().getType()) {
                case IRON_INGOT:
                    ironAmount += item.getItemStack().getAmount();
                case GOLD_INGOT:
                    goldAmount += item.getItemStack().getAmount();
                case EMERALD:
                    emeraldAmount += item.getItemStack().getAmount();
            }
        }

        if (ironAmount < GeneratorType.IRON.getMaxItems()) {
            if ((int)((float)ironTimeTicks/(float) ironMod) <= ironTick) {
                ironTick = 0;
                dropItem(Material.IRON_INGOT);
            }
        }
        if (goldAmount < GeneratorType.GOLD.getMaxItems()) {
            if ((goldTimeTicks/goldMod) <= goldTick) {
                goldTick = 0;
                dropItem(Material.GOLD_INGOT);
            }
        }


        if (spawnEmeralds) {
            if (emeraldAmount < GeneratorType.EMERALD.getMaxItems()) {
                if ((emeraldTimeTicks/emeraldMod) >= emeraldTick) {
                    emeraldTick = 0;
                    dropItem(Material.EMERALD);
                }
            }

        }

    }

}
