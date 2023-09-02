package net.novauniverse.bedwars.game.entity.dragon;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.MobEffectList;
import net.minecraft.server.v1_8_R3.Navigation;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.Bedwars;
import net.novauniverse.bedwars.game.object.BoundingBox;
import net.zeeraa.novacore.commons.utils.ReflectUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.manager.CustomSpectatorManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.SpigotTimings;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BedwarsDragon extends EntityZombie {
	private double speed;

	private int chargeTime;

	private boolean charging;

	private Location location;

	private double radius;

	private double damage;

	private float width;
	private float height;

	public static final long chargeModInTicks = 200;

	public CraftZombie getBukkitEntity() {
		return (CraftZombie) super.getBukkitEntity();
	}

	// sometimes it returned null
	@SuppressWarnings("rawtypes")
	public BedwarsDragon(World world) {
		super(world);

		this.moveController = new ControllerDragon(this);
		this.speed = 1;
		resetCharge();
		charging = false;

		this.width = 16;
		this.height = 8;
		this.damage = 3;

		game = NovaBedwars.getInstance().getGame();

		List goalB = (List) ReflectUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
		goalB.clear();
		List goalC = (List) ReflectUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
		goalC.clear();
		List targetB = (List) ReflectUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
		targetB.clear();
		List targetC = (List) ReflectUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
		targetC.clear();

		goalSelector.a(0, new PathfinderGoalDragonIdle(location, this, radius));
		goalSelector.a(1, new PathfinderGoalDragonCharge(this, null));

		Navigation nav = (Navigation) this.getNavigation();
		nav.a(true);

		this.attachedToPlayer = true;
		this.persistent = true;
		this.valid = true;
		this.fireProof = true;
		this.noclip = true;
		this.location = game.getActiveMap().getSpectatorLocation();
		this.radius = game.getConfig().getDragonRadius();
	}

	@SuppressWarnings("rawtypes")
	public BedwarsDragon(Location location, double radius) {
		super(((CraftWorld) location.getWorld()).getHandle());
		this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		this.location = location;
		this.moveController = new ControllerDragon(this);
		this.speed = 1;
		resetCharge();
		charging = false;
		this.radius = radius;
		this.width = 16;
		this.height = 8;
		this.damage = 3;

		game = NovaBedwars.getInstance().getGame();

		List goalB = (List) ReflectUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
		goalB.clear();
		List goalC = (List) ReflectUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
		goalC.clear();
		List targetB = (List) ReflectUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
		targetB.clear();
		List targetC = (List) ReflectUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
		targetC.clear();

		goalSelector.a(0, new PathfinderGoalDragonIdle(location, this, radius));
		goalSelector.a(1, new PathfinderGoalDragonCharge(this, null));

		Navigation nav = (Navigation) this.getNavigation();
		nav.a(true);

		this.attachedToPlayer = true;
		this.persistent = true;
		this.valid = true;
		this.fireProof = true;
		this.noclip = true;
	}

	public void tick() {
		super.t_();
		this.persistent = true;
		this.valid = true;
		this.attachedToPlayer = true;

		if (!isCharging()) {
			this.decreaseCharge();
			if (!isPositive(this.getChargeTime())) {
				if (Math.random() < (-this.getChargeTime() / ((float) BedwarsDragon.chargeModInTicks / this.getSpeed()))) {
					this.setCharging(true);
				}
			}
		}

		try {
			Field f = Entity.class.getDeclaredField("boundingBox");
			f.setAccessible(true);
			f.set(this, new AxisAlignedBB(locX - (width / 2), locY, locZ - (width / 2), locX + (width / 2), locY + height, locZ + (width / 2)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		AxisAlignedBB aabb = getBoundingBox();

		Vector bottom = new Vector(aabb.a-1, aabb.b-1, aabb.c-1);
		Vector top = new Vector(aabb.d+1, aabb.e+1, aabb.f+1);

		Vector direction = getLocation().clone().getDirection();



		for (Player player : game.getOnlinePlayers()) {
			if (!CustomSpectatorManager.isSpectator(player)) {
				if (player.getNoDamageTicks() <= 0) {
					if (player.getLocation().toVector().isInAABB(bottom, top)) {
						if (player.getGameMode() != GameMode.CREATIVE && !CustomSpectatorManager.isSpectator(player) && player.getGameMode() != GameMode.SPECTATOR) {
							player.damage(damage, this.getBukkitEntity());
							player.setVelocity(player.getVelocity().clone().add(new Vector(direction.getX(), 0.5, direction.getZ())));
						}
					}
				}
			}
		}
	}

	public void destroyNearbyBlocks() {
		AxisAlignedBB aabb = getBoundingBox();

		List<Location> locations = new ArrayList<>();
		for (double x = aabb.a; x <= aabb.d; x++) {
			for (double y = aabb.b; y <= aabb.e; y++) {
				for (double z = aabb.c; z <= aabb.f; z++) {
					locations.add(new Location(world.getWorld(), x, y, z));
				}
			}
		}

		locations.forEach(this::removeBlock);
	}

	public void removeBlock(Location location) {
		if (!BoundingBox.isInside(NovaBedwars.getInstance().getGame().getSafeLocationsWithBeds(), location)) {
			location.getBlock().setType(Material.AIR);
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(GenericAttributes.maxHealth).setValue(200);
		this.setHealth(this.getMaxHealth());
	}

	@SuppressWarnings("unchecked")
	public <T extends PathfinderGoal> T getPathfinderGoalFromClass(Class<T> value) {
		List<?> goalB = (List<?>) ReflectUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);

		return (T) goalB.stream().filter(pf -> {
			try {
				PathfinderGoal pfg = (PathfinderGoal) pf.getClass().getDeclaredField("a").get(pf);
				return pfg.getClass().isAssignableFrom(value);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}).findFirst().orElse(null);
	}

	@SuppressWarnings("unchecked")
	public <T extends PathfinderGoal> T getPathfinderTargetFromClass(Class<T> value) {
		List<?> goalB = (List<?>) ReflectUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);

		return (T) goalB.stream().filter(pf -> {
			try {
				PathfinderGoal pfg = (PathfinderGoal) pf.getClass().getDeclaredField("a").get(pf);
				return pfg.getClass().isAssignableFrom(value);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}).findFirst().orElse(null);
	}

	public void setSpeed(double speed) {
		this.speed = speed;
		resetCharge();
	}

	public double getSpeed() {
		return speed;
	}

	public int getChargeTime() {
		return chargeTime;
	}

	public boolean isCharging() {
		return charging;
	}

	public void setCharging(boolean charging) {
		this.charging = charging;
	}

	public void resetCharge() {
		chargeTime = (int) (Bedwars.CHARGE_TIME_TICKS / speed);
	}

	public void decreaseCharge() {
		chargeTime--;
	}

	public Location getSurroundLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
		this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
		getPathfinderGoalFromClass(PathfinderGoalDragonIdle.class).setMaxDistance(radius);
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public void spawn() {
		WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
		chargeTime = (int) (Bedwars.CHARGE_TIME_TICKS / speed);
		this.prepare(nmsWorld.E(new BlockPosition(this)), null);
		VersionIndependentUtils.get().spawnCustomEntity(this, location);
	}

	public Location getLocation() {
		return new Location(world.getWorld(), locX, locY, locZ, yaw, pitch);
	}

	public Location getEyeLocation() {
		Location loc = this.getLocation();
		loc.setY(loc.getY() + getHeadHeight());
		return loc;
	}

	// allows flight
	@Override
	public void m() {
		activatedTick = MinecraftServer.currentTick;
		try {
			Field f = EntityLiving.class.getDeclaredField("bn");
			f.setAccessible(true);
			int bn = f.getInt(this);

			if (bn > 0) {
				--bn;
				f.setInt(this, bn);
			}

			if (this.bc > 0) {
				double d0 = this.locX + (this.bd - this.locX) / (double) this.bc;
				double d1 = this.locY + (this.be - this.locY) / (double) this.bc;
				double d2 = this.locZ + (this.bf - this.locZ) / (double) this.bc;
				double d3 = MathHelper.g(this.bg - (double) this.yaw);
				this.yaw = (float) ((double) this.yaw + d3 / (double) this.bc);
				this.pitch = (float) ((double) this.pitch + (this.bh - (double) this.pitch) / (double) this.bc);
				--this.bc;
				this.setPosition(d0, d1, d2);
				this.setYawPitch(this.yaw, this.pitch);
			} else if (!this.bM()) {
				this.motX *= 0.98;
				this.motY *= 0.98;
				this.motZ *= 0.98;
			}

			if (Math.abs(this.motX) < 0.005) {
				this.motX = 0.0;
			}

			if (Math.abs(this.motY) < 0.005) {
				this.motY = 0.0;
			}

			if (Math.abs(this.motZ) < 0.005) {
				this.motZ = 0.0;
			}

			this.world.methodProfiler.a("ai");
			SpigotTimings.timerEntityAI.startTiming();
			if (this.bD()) {
				this.aY = false;
				this.aZ = 0.0F;
				this.ba = 0.0F;
				this.bb = 0.0F;
			} else if (this.bM()) {
				this.world.methodProfiler.a("newAi");
				this.doTick();
				this.world.methodProfiler.b();
			}

			SpigotTimings.timerEntityAI.stopTiming();
			this.world.methodProfiler.b();
			this.world.methodProfiler.a("jump");
			if (this.aY) {
				if (this.V()) {
					this.bG();
				} else if (this.ab()) {
					this.bH();
				} else if (this.onGround && bn == 0) {
					this.bF();
					bn = 10;
					f.setInt(this, bn);
				}
			} else {
				bn = 0;
				f.setInt(this, bn);
			}

			this.world.methodProfiler.b();
			this.world.methodProfiler.a("travel");
			this.aZ *= 0.98F;
			this.ba *= 0.98F;
			this.bb *= 0.9F;
			SpigotTimings.timerEntityAIMove.startTiming();
			this.g(this.aZ, this.ba);
			SpigotTimings.timerEntityAIMove.stopTiming();
			this.world.methodProfiler.b();
			this.world.methodProfiler.a("push");
			if (!this.world.isClientSide) {
				SpigotTimings.timerEntityAICollision.startTiming();
				this.bL();
				SpigotTimings.timerEntityAICollision.stopTiming();
			}

			this.world.methodProfiler.b();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void dropDeathLoot(boolean flag, int i) {
	}

	@Override
	protected int getExpValue(EntityHuman entityhuman) {
		return 0;
	}

	@Override
	public boolean k_() {
		return false;
	}

	public void target(Location location) {
		this.getControllerMove().a(location.getX(), location.getY(), location.getZ(), speed);
	}

	private static final int turnMod = 5;

	public void turnTo(Location location) {
		Location toLook = DragonMath.directionFromLocations(getLocation(), location);
		DragonMath.invertDirection(toLook);

		float yawDistance = DragonMath.distanceOfDirectedAngles(getLocation().getYaw(), toLook.getYaw());
		float pitchDistance = DragonMath.distanceOfDirectedAngles(getLocation().getPitch(), toLook.getPitch());

		int turn;
		if (Math.abs(yawDistance) >= Math.abs(pitchDistance)) {
			turn = (int) Math.abs(Math.ceil(yawDistance / (turnMod * speed)));
		} else {
			turn = (int) Math.abs(Math.ceil(pitchDistance / (turnMod * speed)));
		}

		float yawMod = DragonMath.asRealNumber(yawDistance / turn);
		float pitchMod = DragonMath.asRealNumber(pitchDistance / turn);
		int[] currentTick = new int[] { 0 };

		final float[] currentYaw = { getLocation().getYaw() };
		final float[] currentPitch = { getLocation().getPitch() };
		new BukkitRunnable() {
			@Override
			public void run() {
				// on end
				if (currentTick[0] >= turn) {
					cancel();
					target(location);
					Location toLook = DragonMath.directionFromLocations(getLocation(), location);
					DragonMath.invertDirection(toLook);
					BedwarsDragon.this.lastYaw = BedwarsDragon.this.yaw = toLook.getYaw();
					BedwarsDragon.this.lastPitch = BedwarsDragon.this.pitch = toLook.getPitch();
					return;
				}

				Location toLook = getLocation().clone();
				currentYaw[0] = DragonMath.fixDirectedAngle(DragonMath.sumDirectedAngle(currentYaw[0], yawMod));
				currentPitch[0] = DragonMath.fixDirectedAngle(DragonMath.sumDirectedAngle(currentPitch[0], pitchMod));
				toLook.setYaw(currentYaw[0]);
				toLook.setPitch(currentPitch[0]);

				BedwarsDragon.this.lastYaw = BedwarsDragon.this.yaw = toLook.getYaw();
				BedwarsDragon.this.lastPitch = BedwarsDragon.this.pitch = toLook.getPitch();
				Vector lineOfSight = getEyeLocation().getDirection().normalize();
				lineOfSight.multiply(speed);
				toLook.add(lineOfSight);
				target(toLook);
				currentTick[0]++;
			}
		}.runTaskTimer(NovaBedwars.getInstance(), 0, 1);
	}

	public void g(float var1, float var2) {
		if (this.V()) {
			this.a(var1, var2, 0.02F);
			this.move(this.motX, this.motY, this.motZ);
			this.motX *= 0.800000011920929;
			this.motY *= 0.800000011920929;
			this.motZ *= 0.800000011920929;
		} else if (this.ab()) {
			this.a(var1, var2, 0.02F);
			this.move(this.motX, this.motY, this.motZ);
			this.motX *= 0.5;
			this.motY *= 0.5;
			this.motZ *= 0.5;
		} else {
			float var3 = 0.91F;
			if (this.onGround) {
				var3 = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ))).getBlock().frictionFactor * 0.91F;
			}

			float var4 = 0.16277136F / (var3 * var3 * var3);
			this.a(var1, var2, this.onGround ? 0.1F * var4 : 0.02F);
			var3 = 0.91F;
			if (this.onGround) {
				var3 = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ))).getBlock().frictionFactor * 0.91F;
			}

			this.move(this.motX, this.motY, this.motZ);
			this.motX *= (double) var3;
			this.motY *= (double) var3;
			this.motZ *= (double) var3;
		}

		this.aA = this.aB;
		double var5 = this.locX - this.lastX;
		double var7 = this.locZ - this.lastZ;
		float var9 = MathHelper.sqrt(var5 * var5 + var7 * var7) * 4.0F;
		if (var9 > 1.0F) {
			var9 = 1.0F;
		}

		this.aB += (var9 - this.aB) * 0.4F;
		this.aC += this.aB;
	}

	@Override
	public void e(float var1, float var2) {
	}

	@Override
	protected void a(double var1, boolean var3, Block var4, BlockPosition var5) {
	}

	@Override
	public boolean cp() {
		return false;
	}

	private boolean isPositive(double num) {
		return num >= 0;
	}

	@Override
	protected String z() {
		return "mob.enderdragon.growl";
	}

	@Override
	protected String bo() {
		return "mob.enderdragon.hit";
	}

	@Override
	protected String bp() {
		return "mob.enderdragon.death";
	}

	private Bedwars game;

	@Override
	public void t_() {

	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (this.isInvulnerable(damagesource)) {
			return false;
		} else if (this.world.isClientSide) {
			return false;
		} else {
			this.ticksFarFromPlayer = 0;
			if (this.getHealth() <= 0.0F) {
				return false;
			} else if (damagesource.o() && this.hasEffect(MobEffectList.FIRE_RESISTANCE)) {
				return false;
			} else {
				this.aB = 1.5F;
				boolean flag = true;
				if ((float) this.noDamageTicks > (float) this.maxNoDamageTicks / 2.0F) {
					if (f <= this.lastDamage) {
						this.forceExplosionKnockback = true;
						return false;
					}

					if (!this.d(damagesource, f - this.lastDamage)) {
						return false;
					}

					this.lastDamage = f;
					flag = false;
				} else {
					this.getHealth();
					if (!this.d(damagesource, f)) {
						return false;
					}

					this.lastDamage = f;
					this.noDamageTicks = this.maxNoDamageTicks;
					this.hurtTicks = this.av = 10;
				}

				this.aw = 0.0F;
				Entity entity = damagesource.getEntity();
				if (entity != null) {
					if (entity instanceof EntityLiving) {
						this.b((EntityLiving) entity);
					}

					if (entity instanceof EntityHuman) {
						this.lastDamageByPlayerTime = 100;
						this.killer = (EntityHuman) entity;
					} else if (entity instanceof EntityWolf) {
						EntityWolf entitywolf = (EntityWolf) entity;
						if (entitywolf.isTamed()) {
							this.lastDamageByPlayerTime = 100;
							this.killer = null;
						}
					}
				}

				if (flag) {
					this.world.broadcastEntityEffect(this, (byte) 2);
					if (damagesource != DamageSource.DROWN) {
						this.ac();
					}

					if (entity != null) {
						double d0 = entity.locX - this.locX;

						double d1;
						for (d1 = entity.locZ - this.locZ; d0 * d0 + d1 * d1 < 1.0E-4; d1 = (Math.random() - Math.random()) * 0.01) {
							d0 = (Math.random() - Math.random()) * 0.01;
						}

						this.aw = (float) (MathHelper.b(d1, d0) * 180.0 / 3.1415927410125732 - (double) this.yaw);
						this.a(entity, f, d0, d1);
					} else {
						this.aw = (float) ((int) (Math.random() * 2.0) * 180);
					}
				}

				String s;
				if (this.getHealth() <= 0.0F) {
					s = this.bp();
					if (flag && s != null) {
						this.makeSound(s, this.bB(), this.bC());
					}

					this.die(damagesource);
				} else {
					s = this.bo();
					if (flag && s != null) {
						this.makeSound(s, this.bB(), this.bC());
					}
				}

				return true;
			}
		}
	}
}
