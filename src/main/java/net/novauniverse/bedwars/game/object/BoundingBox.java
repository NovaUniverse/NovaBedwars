package net.novauniverse.bedwars.game.object;

import org.bukkit.Location;

import java.util.Collection;
import java.util.List;

public class BoundingBox {
    private Location bottom;
    private Location top;

    public BoundingBox(Location bottom, Location top) {
        this.bottom = bottom;
        this.top = top;
    }

    public Location getBottom() {
        return bottom;
    }

    public Location getTop() {
        return top;
    }

    public boolean isInside(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        return (x >= bottom.getX() && x <= top.getX()) && (y >= bottom.getY() && y <= top.getY()) && (z >= bottom.getZ() && z <= top.getZ());
    }
    public static boolean isInside(Collection<BoundingBox> boxes, Location location) {
        boolean[] answer = new boolean[]{false};
        boxes.forEach(boundingBox -> {
            if (boundingBox.isInside(location)) {
                answer[0] = true;
            }
        });
        return answer[0];
    }

}
