package com.electroblob.wizardry.api.client.particle;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.entity.ICustomHitbox;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Abstract superclass for all of wizardry's particles.
 * <p></p>
 * The new system is as follows:
 * <p></p>
 * - All particle classes have a single constructor which takes a world and a position only.<br>
 * - Each particle class defines any relevant default values in its constructor, including velocity.<br>
 * - The particle builder then overwrites any other values that were set during building.
 * <p></p>
 *
 * @see ParticleBuilder ParticleBuilder
 */

public abstract class ParticleWizardry extends TextureSheetParticle {
    public static final Map<DeferredObject<SimpleParticleType>, BiFunction<ClientLevel, Vec3, ParticleWizardry>> PROVIDERS = new LinkedHashMap<>();

    /** The sprite of the particle. */
    SpriteSet spriteSet;

    // ------------------------- Some field properties -------------------------------- //
    /** A long value used by the renderer as a random number seed, ensuring anything that is randomized remains the
     * same across multiple frames. For example, lightning particles use this to keep their shape across ticks.
     * This value can also be set during particle creation, allowing users to keep randomized properties the same
     * even across multiple particles. If unspecified, the seed is chosen at random. */
    protected long seed;

    protected Random random = new Random(); // If we're not using a seed, this defaults to any old seed

    /** True if the particle is shaded, false if the particle always renders at full brightness. Defaults to false. */
    protected boolean shaded = false;

    protected float initialRed;
    protected float initialGreen;
    protected float initialBlue;

    protected float fadeRed = 0;
    protected float fadeGreen = 0;
    protected float fadeBlue = 0;

    protected float angle;
    protected double radius = 0;
    protected double speed = 0;

    /** The entity this particle is linked to. The particle will move with this entity. */
    @Nullable
    protected Entity entity = null;
    /** Coordinates of this particle relative to the linked entity. If the linked entity is null, these are used as
     * the absolute coordinates of the centre of rotation for particles with spin. If the particle has neither a
     * linked entity nor spin, these are not used. */
    protected double relativeX, relativeY, relativeZ;
    /** Velocity of this particle relative to the linked entity. If the linked entity is null, these are not used. */
    protected double relativeMotionX, relativeMotionY, relativeMotionZ;
    /** The yaw angle this particle is facing, or {@code NaN} if this particle always faces the viewer (default behaviour). */
    protected float yaw = Float.NaN;
    /** The pitch angle this particle is facing, or {@code NaN} if this particle always faces the viewer (default behaviour). */
    protected float pitch = Float.NaN;

    /** The fraction of the impact velocity that should be the maximum spread speed added on impact. */
    private static final double SPREAD_FACTOR = 0.2;
    /** Lateral velocity is reduced by this factor on impact, before adding random spread velocity. */
    private static final double IMPACT_FRICTION = 0.2;

    /** Previous-tick velocity, used in collision detection. */
    private double prevVelX, prevVelY, prevVelZ;
    protected boolean adjustQuadSize;
    private final boolean updateTextureOnTick;


    public ParticleWizardry(ClientLevel world, double x, double y, double z, SpriteSet spriteSet, boolean updateTextureOnTick) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.relativeX = this.x;
        this.relativeY = this.y;
        this.relativeZ = this.z;
        this.updateTextureOnTick = updateTextureOnTick;
        this.setSpriteFromAge(spriteSet);
    }

    // ============================================== Parameter Setters ==============================================

    // Setters for parameters that affect all particles - these are implemented in this class (although they may be
    // reimplemented in subclasses)

    /** Sets the seed for this particle's randomly generated values and resets {@link ParticleWizardry#random} to use
     * that seed. Implementations will differ between particle types; for example, ParticleLightning has an update
     * period which changes the seed every few ticks, whereas ParticleVine simply retains the same seed for its entire
     * lifetime. */
    public void setSeed(long seed){
        this.seed = seed;
        this.random = new Random(seed);
    }

    /** Sets whether the particle should render at full brightness or not. True if the particle is shaded, false if
     * the particle always renders at full brightness. Defaults to false.*/
    public void setShaded(boolean shaded){
        this.shaded = shaded;
    }

    /** Sets this particle's gravity. True to enable gravity, false to disable. Defaults to false.*/
    public void setGravity(boolean gravity){
        this.gravity = gravity ? 1 : 0;
    }

    /** Sets this particle's collisions. True to enable block collisions, false to disable. Defaults to false.*/
    public void setCollisions(boolean canCollide){
        this.hasPhysics = canCollide;
    }

    /**
     * Sets the velocity of the particle.
     * @param velocityX The x velocity
     * @param velocityY The y velocity
     * @param velocityZ The z velocity
     */
    @Override
    public void setParticleSpeed(double velocityX, double velocityY, double velocityZ) {
        super.setParticleSpeed(velocityX, velocityY, velocityZ);
    }

    /**
     * Sets the spin parameters of the particle.
     * @param radius The spin radius
     * @param speed The spin speed in rotations per tick
     */
    public void setSpin(double radius, double speed){
        this.radius = radius;
        this.speed = speed * 2 * Math.PI; // Converts rotations per tick into radians per tick for the trig functions
        this.angle = this.random.nextFloat() * (float)Math.PI * 2; // Random start angle
        this.x = relativeX - radius * Mth.cos(angle);
        this.z = relativeZ + radius * Mth.sin(angle);

        // Set these to the correct values
        this.relativeMotionX = xd;
        this.relativeMotionY = yd;
        this.relativeMotionZ = zd;
    }

    /**
     * Links this particle to the given entity. This will cause its position and velocity to be relative to the entity.
     * @param entity The entity to link to.
     */
    public void setEntity(Entity entity){
        this.entity = entity;
        // Set these to the correct values
        if(entity != null){
            this.setPos(this.entity.xo + relativeX, this.entity.yo
                    + relativeY, this.entity.zo + relativeZ);
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            // Set these to the correct values
            this.relativeMotionX = xd;
            this.relativeMotionY = yd;
            this.relativeMotionZ = zd;
        }
    }

    /**
     * Sets the base color of the particle. <i>Note that this also sets the fade colour so that particles without a
     * fade colour do not change colour at all; as such fade colour must be set <b>after</b> calling this method.</i>
     * @param red The red color component
     * @param green The green color component
     * @param blue The blue colour component
     */
    @Override
    public void setColor(float red, float green, float blue) {
        super.setColor(red, green, blue);
        initialRed = red;
        initialGreen = green;
        initialBlue = blue;
        // If fade color is not specified, it defaults to the main color - this method is always called first
        setFadeColour(red, green, blue);
    }

    /**
     * Sets the fade color of the particle.
     * @param r The red color component
     * @param g The green color component
     * @param b The blue colour component
     */
    public void setFadeColour(float r, float g, float b){
        this.fadeRed = r;
        this.fadeGreen = g;
        this.fadeBlue = b;
    }

    /**
     * Sets the direction this particle faces. This will cause the particle to render facing the given direction.
     * @param yaw The yaw angle of this particle in degrees, where 0 is south.
     * @param pitch The pitch angle of this particle in degrees, where 0 is horizontal.
     */
    public void setFacing(float yaw, float pitch){
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Sets the target position for this particle. This will cause it to stretch to touch the given position,
     * if supported.
     * @param x The x-coordinate of the target position.
     * @param y The y-coordinate of the target position.
     * @param z The z-coordinate of the target position.
     */
    public void setTargetPosition(double x, double y, double z){
        // Does nothing for normal particles since normal particles always render at a single point
    }

    /**
     * Sets the target point velocity for this particle. This will cause the position it stretches to touch to move
     * at the given velocity.
     * @param vx The x velocity of the target point.
     * @param vy The y velocity of the target point.
     * @param vz The z velocity of the target point.
     */
    public void setTargetVelocity(double vx, double vy, double vz){
        // Does nothing for normal particles since normal particles always render at a single point
    }

    /**
     * Links this particle to the given target. This will cause it to stretch to touch the target, if supported.
     * @param target The target to link to.
     */
    public void setTargetEntity(Entity target){
        // Does nothing for normal particles since normal particles always render at a single point
    }

    /**
     * Sets the length of this particle. This will cause it to stretch to touch a point this distance along its
     * linked entity's line of sight.
     * @param length The length to set.
     */
    public void setLength(double length){
        // Does nothing for normal particles since normal particles always render at a single point
    }

    // ============================================== Method Overrides ==============================================
    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float f) {
        return shaded ? super.getLightColor(f) : 15728880;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Entity viewer = camera.getEntity();

        updateEntityLinking(viewer, tickDelta);

        if (Float.isNaN(this.yaw) || Float.isNaN(this.pitch)) {
            super.render(vertexConsumer, camera, tickDelta);
        } else {
            float degToRadFactor = 0.017453292f;

            float rotationX = Mth.cos(yaw * degToRadFactor);
            float rotationZ = Mth.sin(yaw * degToRadFactor);
            float rotationY = Mth.cos(pitch * degToRadFactor);
            float rotationYZ = -rotationZ * Mth.sin(pitch * degToRadFactor);
            float rotationXY = rotationX * Mth.sin(pitch * degToRadFactor);

            drawParticle(vertexConsumer, camera, tickDelta, rotationX, rotationY, rotationZ, rotationYZ, rotationXY);
        }
    }

    protected void drawParticle(VertexConsumer buffer, Camera camera, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ){
        Vec3 vec3 = camera.getPosition();

        float s = this.adjustQuadSize ? 0.1f : 1;
        float f4 = s * this.getQuadSize(partialTicks);

        float f = this.getU0();
        float f1 = this.getU1();
        float f2 = this.getV0();
        float f3 = this.getV1();

        float f5 = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f6 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f7 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());

        int i = this.getLightColor(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;

        Vec3[] avec3 = new Vec3[]{new Vec3(-rotationX * f4 - rotationXY * f4, -rotationZ * f4, -rotationYZ * f4 - rotationX * f4),
                new Vec3(-rotationX * f4 + rotationXY * f4, rotationZ * f4, -rotationYZ * f4 + rotationXZ * f4),
                new Vec3(rotationX * f4 + rotationXY * f4, rotationZ * f4, rotationYZ * f4 + rotationXZ * f4),
                new Vec3(rotationX * f4 - rotationXY * f4, -rotationZ * f4, rotationYZ * f4 - rotationXZ * f4)};

        if (this.angle != 0.0F) {
            float f8 = this.angle + (this.angle - this.oRoll) * partialTicks;
            float f9 = Mth.cos(f8 * 0.5F);
            float f10 = Mth.sin(f8 * 0.5F) * camera.rotation().x();
            float f11 = Mth.sin(f8 * 0.5F) * camera.rotation().y();
            float f12 = Mth.sin(f8 * 0.5F) * camera.rotation().z();
            Vec3 vec3d = new Vec3(f10, f11, f12);

            for (int l = 0; l < 4; ++l) {
                avec3[l] = vec3d.scale(2.0D * avec3[l].dot(vec3d)).add(avec3[l].scale((double) (f9 * f9) - vec3d.dot(vec3d))).add(vec3d.cross(avec3[l]).scale(2.0F * f9));
            }
        }

        buffer.vertex((double) f5 + avec3[0].x, (double) f6 + avec3[0].y, (double) f7 + avec3[0].z).uv(f1, f3).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j, k).endVertex();
        buffer.vertex((double) f5 + avec3[1].x, (double) f6 + avec3[1].y, (double) f7 + avec3[1].z).uv(f1, f2).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j, k).endVertex();
        buffer.vertex((double) f5 + avec3[2].x, (double) f6 + avec3[2].y, (double) f7 + avec3[2].z).uv(f, f2).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j, k).endVertex();
        buffer.vertex((double) f5 + avec3[3].x, (double) f6 + avec3[3].y, (double) f7 + avec3[3].z).uv(f, f3).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j, k).endVertex();
    }


    protected void updateEntityLinking(Entity viewer, float partialTicks) {
        if (this.entity != null) {
            x = x + entity.xo - entity.getX() - relativeMotionX * (1 - partialTicks);
            y = y + entity.yo - entity.getY() - relativeMotionY * (1 - partialTicks);
            z = z + entity.zo - entity.getZ() - relativeMotionZ * (1 - partialTicks);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.hasPhysics && this.onGround) {
            this.xd /= 0.699999988079071D;
            this.zd /= 0.699999988079071D;
        }

        if (entity != null || radius > 0) {
            double x = relativeX;
            double y = relativeY;
            double z = relativeZ;

            if (this.entity != null) {
                if (!this.entity.isAlive()) {
                    this.remove();
                } else {
                    x += this.entity.getX();
                    y += this.entity.getY();
                    z += this.entity.getZ();
                }
            }

            if (radius > 0) {
                angle += (float) speed;
                x += radius * -Mth.cos(angle);
                z += radius * Mth.sin(angle);
            }

            this.setPos(x, y, z);

            this.relativeX += relativeMotionX;
            this.relativeY += relativeMotionY;
            this.relativeZ += relativeMotionZ;
        }

        float ageFraction = (float) this.age / (float) this.lifetime;

        this.rCol = this.initialRed + (this.fadeRed - this.initialRed) * ageFraction;
        this.gCol = this.initialGreen + (this.fadeGreen - this.initialGreen) * ageFraction;
        this.bCol = this.initialBlue + (this.fadeBlue - this.initialBlue) * ageFraction;

        if (hasPhysics) {
            if (this.xd == 0 && this.prevVelX != 0) {
                this.yd *= IMPACT_FRICTION;
                this.zd *= IMPACT_FRICTION;

                this.yd += (random.nextDouble() * 2 - 1) * this.prevVelX * SPREAD_FACTOR;
                this.zd += (random.nextDouble() * 2 - 1) * this.prevVelX * SPREAD_FACTOR;
            }

            if (this.yd == 0 && this.prevVelY != 0) {
                this.xd *= IMPACT_FRICTION;
                this.zd *= IMPACT_FRICTION;

                this.xd += (random.nextDouble() * 2 - 1) * this.prevVelY * SPREAD_FACTOR;
                this.zd += (random.nextDouble() * 2 - 1) * this.prevVelY * SPREAD_FACTOR;
            }

            if (this.zd == 0 && this.prevVelZ != 0) {
                this.xd *= IMPACT_FRICTION;
                this.yd *= IMPACT_FRICTION;

                this.xd += (random.nextDouble() * 2 - 1) * this.prevVelZ * SPREAD_FACTOR;
                this.yd += (random.nextDouble() * 2 - 1) * this.prevVelZ * SPREAD_FACTOR;
            }

            double searchRadius = 20;

            List<Entity> nearbyEntities = EntityUtil.getEntitiesWithinRadius(searchRadius, this.x, this.y, this.z, level, Entity.class);

            if (nearbyEntities.stream().anyMatch(e -> e instanceof ICustomHitbox && ((ICustomHitbox) e).calculateIntercept(new Vec3(x, y, z), new Vec3(x, y, z), 0) != null))
                this.remove();
        }

        this.prevVelX = xd;
        this.prevVelY = yd;
        this.prevVelZ = zd;

        if (updateTextureOnTick) {
            this.setSpriteFromAge(spriteSet);
        }
    }

    // Overridden and copied to fix the collision behavior
    @Override
    public void move(double dx, double dy, double dz) {
        double d0 = dx;
        double d1 = dy;
        double d2 = dz;
        if (this.hasPhysics && (dx != 0.0D || dy != 0.0D || dz != 0.0D) && dx * dx + dy * dy + dz * dz < Mth.square(100.0D)) {
            Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(dx, dy, dz), this.getBoundingBox(), this.level, List.of());
            dx = vec3.x;
            dy = vec3.y;
            dz = vec3.z;
        }

        if (dx != 0.0D || dy != 0.0D || dz != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
            this.setLocationFromBoundingbox();
        }

        this.onGround = d1 != dy && d1 < 0.0D;

        if (d0 != dx) {
            this.xd = 0.0D;
        }

        if (d1 != dy) {
            this.yd = 0.0D;
        }

        if (d2 != dz) {
            this.zd = 0.0D;
        }
    }


}