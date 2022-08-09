package net.brunogamer.bedwars.game.entity;

import net.brunogamer.bedwars.NovaBedwars;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public abstract class BedwarsNPCS {
    protected Location location;
    public BedwarsNPCS(Location location) {
        this.location = location;
    }
    public abstract void spawn();
    public void noAI(Entity bukkitEntity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        nmsEntity.c(tag);
        tag.setInt("NoAI", 1);
        nmsEntity.f(tag);
    }
    public void lookAtPlayerRunnable(Entity entity) {
       BukkitRunnable runnable = new BukkitRunnable() {

           @Override
           public void run() {
               entity.getWorld().getNearbyEntities(entity.getLocation(), 3,3,3).forEach(entity1 ->{
                   if (entity1 instanceof Player) {
                       Player player = (Player) entity1;
                       Vector vec = player.getLocation().toVector().subtract(entity.getLocation().toVector());
                       Location location = entity.getLocation().setDirection(vec);
                       location.setX(entity.getLocation().getX());
                       location.setY(entity.getLocation().getY());
                       location.setZ(entity.getLocation().getZ());
                       entity.teleport(location);
                   }
               });
           }
       };
       runnable.runTaskTimer(NovaBedwars.getInstance(),0,1);
    }
}
