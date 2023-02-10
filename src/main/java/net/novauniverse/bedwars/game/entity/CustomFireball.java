package net.novauniverse.bedwars.game.entity;

import net.minecraft.server.v1_8_R3.EntityLargeFireball;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.World;

public class CustomFireball extends EntityLargeFireball {

    boolean allowDamage;

    public CustomFireball(World world, EntityLiving entityliving, double d0, double d1, double d2) {
        super(world, entityliving, d0, d1, d2);
        allowDamage = false;
    }

    public boolean isAllowDamage() {
        return allowDamage;
    }

    public void setAllowDamage(boolean allowDamage) {
        this.allowDamage = allowDamage;
    }

}
