package net.novauniverse.bedwars.game.entity.dragon;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.ControllerMove;
import net.minecraft.server.v1_8_R3.MathHelper;

public class ControllerDragon extends ControllerMove {

    private BedwarsDragon dragon;

    public ControllerDragon(BedwarsDragon bwd) {
        super(bwd);
        this.dragon = bwd;
    }

    // tick
    public void c() {
        if (this.f) {
            double distanceX = this.b - this.dragon.locX;
            double distanceY = this.c - this.dragon.locY;
            double distanceZ = this.d - this.dragon.locZ;
            double distance = Math.pow(distanceX, 2) + Math.pow(distanceY, 2) + Math.pow(distanceZ, 2);
            distance = MathHelper.sqrt(distance);
            if (distance >= dragon.getSpeed()/2 || distance <= -(dragon.getSpeed()/2)) {
                BedwarsDragon var10000 = this.dragon;
                var10000.motX += (distanceX / distance * 0.1) * dragon.getSpeed();
                var10000.motY += (distanceY / distance * 0.1) * dragon.getSpeed();
                var10000.motZ += (distanceZ / distance * 0.1) * dragon.getSpeed();
            } else {
                this.f = false;
            }
        }
    }
}
