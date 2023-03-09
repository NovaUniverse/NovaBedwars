package net.novauniverse.bedwars.game.entity.dragon;

import org.bukkit.Location;

public class DragonMath {

    public static float asRealNumber(float num) {
        if (Float.isInfinite(num) || Float.isNaN(num)) {
            return 0;
        } else {
            return num;
        }
    }

    public static float distanceOfDirectedAngles(float from, float to) {
        float from360 = from + 180;
        float to360 = to + 180;
        float opt1 = to360 - from360;
        float opt2 = (360 - from360) + to360;
        return getSmallest(opt1, opt2);
    }

    public static float getSmallest(float... floats) {
        if (floats.length == 0) {
            return Float.MAX_VALUE;
        }
        float current = Float.MAX_VALUE;
        float currentAbs = Float.MAX_VALUE;
        for (float f : floats) {
            if (Math.abs(f) <= currentAbs) {
                current = f;
                currentAbs = Math.abs(f);
            }
        }
        return current;
    }

    public static boolean isPositive(Number number) {
        return number.doubleValue() >= 0;
    }

    public static boolean isNegative(Number number) {
        return !isPositive(number);
    }

    public static float sumDirectedAngle(float degree180, float sum) {
        return degree180 + sum;
    }

    public static float fixDirectedAngle(float dirAngle) {
        return dirAngle > 180 ? -360 + dirAngle : dirAngle < -180 ? 360 + dirAngle : dirAngle;
    }

    public static Location directionFromLocations(Location from, Location to) {
        double xDiff = to.getX() - from.getX();
        double yDiff = to.getY() - from.getY();
        double zDiff = to.getZ() - from.getZ();

        double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
        double newYaw = Math.acos(xDiff / DistanceXZ) * 180 / Math.PI;
        double newPitch = Math.acos(yDiff / DistanceY) * 180 / Math.PI - 90;
        if (zDiff < 0.0)
            newYaw = newYaw + Math.abs(180 - newYaw) * 2;
        newYaw = (newYaw - 90);

        return new Location(null, 0,0,0,(float) newYaw, (float) newPitch);
    }

    public static void invertDirection(Location location) {
        location.setYaw(DragonMath.invertDirectedAngle(location.getYaw()));
        location.setPitch(DragonMath.invertDirectedAngle(location.getPitch()));
    }

    public static float invertDirectedAngle(float yawPitch) {
        return yawPitch < 0 ? yawPitch +180 : yawPitch-180;
    }
}
