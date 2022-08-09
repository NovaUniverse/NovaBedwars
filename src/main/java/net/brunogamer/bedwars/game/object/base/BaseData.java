package net.brunogamer.bedwars.game.object.base;

import net.brunogamer.bedwars.game.object.Trap;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.utils.XYZLocation;
import org.bukkit.Location;

import java.util.ArrayList;

public class BaseData {
    private Team owner;

    private int protectionLevel;
    private int hasteLevel;
    private int forgeLevel;

    private boolean sharpness;
    private boolean healPool;
    private boolean hasBed;

    private Location spawnLocation;
    private Location bedLocation;

    private XYZLocation itemShopLocation;
    private XYZLocation upgradeShopLocation;

    private ArrayList<Trap> traps;
    public BaseData(Team owner, Location spawnLocation, Location bedLocation, XYZLocation itemShopLocation, XYZLocation upgradeShopLocation) {
        this.owner = owner;
        this.spawnLocation = spawnLocation;
        this.bedLocation = bedLocation;
        this.itemShopLocation = itemShopLocation;
        this.upgradeShopLocation = upgradeShopLocation;
    }
    public Team getOwner() {
        return owner;
    }
    public int getProtectionLevel() {
        return protectionLevel;
    }
    public int getHasteLevel() {
        return hasteLevel;
    }
    public int getForgeLevel() {
        return forgeLevel;
    }
    public boolean hasSharpness() {
        return sharpness;
    }
    public boolean hasHealPool() {
        return healPool;
    }
    public boolean hasBed() {
        return hasBed;
    }
    public Location getSpawnLocation() {
        return spawnLocation;
    }
    public Location getBedLocation() {
        return bedLocation;
    }
    public XYZLocation getItemShopLocation() {
        return itemShopLocation;
    }
    public XYZLocation getUpgradeShopLocation() {
        return upgradeShopLocation;
    }
    public void setOwner(Team owner) {
        this.owner = owner;
    }
    public void setProtectionLevel(int level) {
        this.protectionLevel = level;
    }
    public void setHasteLevel(int hasteLevel) {
        this.hasteLevel = hasteLevel;
    }
    public void setForgeLevel(int forgeLevel) {
        this.forgeLevel = forgeLevel;
    }
    private void setSharpness(boolean sharpness) {
        this.sharpness = sharpness;
    }
    public void setHealPool(boolean healPool) {
        this.healPool = healPool;
    }
    private void setBed(boolean bed) {
        this.hasBed = bed;
    }
    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
    }
    public void setBedLocation(Location location) {
        this.bedLocation = location;
    }
    public void setItemShopLocation(XYZLocation itemShopLocation) {
        this.itemShopLocation = itemShopLocation;
    }
    public void setUpgradeShopLocation(XYZLocation upgradeShopLocation) {
        this.upgradeShopLocation = upgradeShopLocation;
    }
    public void setTraps(ArrayList<Trap> traps) {
        this.traps = traps;
    }
    public void addTrap(Trap trap) {
        this.traps.add(trap);
    }

}
