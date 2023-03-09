package net.novauniverse.bedwars.game.entity.dragon;

import net.minecraft.server.v1_8_R3.ControllerMove;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.zeeraa.novacore.commons.utils.RandomGenerator;
import org.bukkit.Location;

import java.util.Random;

public class PathfinderGoalDragonIdle extends PathfinderGoal {


    private Location toRoam;

    private BedwarsDragon owner;

    private double maxDistance;

    public PathfinderGoalDragonIdle(Location toRoam, BedwarsDragon owner, double maxDistance) {
        this.toRoam = toRoam;
        this.owner = owner;
        this.maxDistance = maxDistance;
    }

    public void setMaxDistance(double distance) {
        this.maxDistance = distance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public Location getToRoam() {
        return toRoam;
    }

    public BedwarsDragon getOwner() {
        return owner;
    }


    @Override
    public boolean a() {
        ControllerMove controllermove = this.owner.getControllerMove();
        if (!controllermove.a()) {
            return owner.isAlive() && !owner.isCharging();
        } else {
            return false;
        }
    }

    @Override
    public boolean b() {
        return false;
    }

    double previousX = -2;
    double previousZ = -2;
    @Override
    public void c() {
        Random random = new Random();
        double xMod = RandomGenerator.generateDouble(-1,1, random);
        double yMod = RandomGenerator.generateDouble(-1,1, random);
        double zMod = RandomGenerator.generateDouble(-1,1, random);
        while (true) {
            if (isSameQuadrant(xMod, previousX) || isSameQuadrant(zMod, previousZ)) {
                if (isSameQuadrant(xMod, previousX) && isSameQuadrant(zMod, previousZ)) {
                    byte whichToChoose = (byte) RandomGenerator.generate(0,2, random);
                    if (distance(xMod, previousX) <= 0.4 && distance(zMod, previousZ) <= 0.4) {
                        whichToChoose = 2;
                    } else if (distance(xMod, previousX) <= 0.4) {
                        whichToChoose = 0;
                    } else if (distance(zMod, previousX) <= 0.4) {
                        whichToChoose = 1;
                    }

                    if (whichToChoose == 0) {
                        xMod = RandomGenerator.generateDouble(-1,1, random);
                    } else if (whichToChoose == 1) {
                        zMod = RandomGenerator.generateDouble(-1,1, random);
                    } else {
                        xMod = RandomGenerator.generateDouble(-1,1, random);
                        zMod = RandomGenerator.generateDouble(-1,1, random);
                    }
                } else if (isSameQuadrant(xMod, previousX)) {
                    xMod = RandomGenerator.generateDouble(-1,1, random);
                } else if (isSameQuadrant(zMod, previousZ)) {
                    zMod = RandomGenerator.generateDouble(-1,1, random);
                }
            } else {
                if (distance(xMod, previousX) <= 0.2 || distance(xMod, previousX) <= 0.2) {
                    if (distance(xMod, previousX) <= 0.2 && distance(xMod, previousX) <= 0.2) {
                        byte whichToChoose = (byte) RandomGenerator.generate(0,2, random);
                        if (whichToChoose == 0) {
                            xMod = RandomGenerator.generateDouble(-1,1, random);
                        } else if (whichToChoose == 1) {
                            zMod = RandomGenerator.generateDouble(-1,1, random);
                        } else {
                            xMod = RandomGenerator.generateDouble(-1,1, random);
                            zMod = RandomGenerator.generateDouble(-1,1, random);
                        }
                    } else if (distance(xMod, previousX) <= 0.2) {
                        xMod = RandomGenerator.generateDouble(-1,1, random);
                    } else if (distance(xMod, previousX) <= 0.2) {
                        zMod = RandomGenerator.generateDouble(-1,1, random);
                    }
                } else {
                    break;
                }
            }

            if (distance(xMod, previousX) <= 0.3 && distance(zMod, previousZ) <= 0.3) {
                byte whichToChoose = (byte) RandomGenerator.generate(0,1);
                if (whichToChoose == 0) {
                    xMod = RandomGenerator.generateDouble(-1,1);
                } else {
                    zMod = RandomGenerator.generateDouble(-1,1);
                }
            } else {
                if (distance(xMod, previousX) <= 0.3 ) {
                    xMod = RandomGenerator.generateDouble(-1,1);
                } else if (distance(zMod, previousZ) <= 0.3) {
                    zMod = RandomGenerator.generateDouble(-1,1);
                } else {
                    break;
                }
            }
        }
        previousX = xMod;
        previousZ = zMod;
        Location loc = toRoam.clone();
        loc.add(maxDistance*xMod, 10*yMod, maxDistance*zMod);
        owner.turnTo(loc);
    }

    private double distance(double d1, double d2) {
        return d1 <= d2 ? asPositive(d1 - d2) : asPositive(d2 - d1);
    }

    private boolean isPositive(double num) {
        return num >= 0;
    }

    public double asPositive(double value) {
        return value >= 0 ? value : -value;
    }

    public boolean isSameQuadrant(double num, double otherNum) {
        return (isPositive(num) && isPositive(otherNum)) || (!isPositive(num) && !isPositive(otherNum));
    }
}
