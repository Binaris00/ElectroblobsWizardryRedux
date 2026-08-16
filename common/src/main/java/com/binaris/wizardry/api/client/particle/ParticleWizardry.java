package com.binaris.wizardry.api.client.particle;

import com.binaris.wizardry.api.client.ParticleBuilder;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

/// Abstract superclass for all of wizardry's particles.
///
/// The new system is as follows:
/// - All particle classes have a single constructor which takes a world and a position only.
/// - Each particle class defines any relevant default values in its constructor, including velocity.
/// - The particle builder then overwrites any other values that were set during building.
///
/// @see ParticleBuilder ParticleBuilder
public abstract class ParticleWizardry extends TextureSheetParticle {
    public static final Map<SimpleParticleType, BiFunction<ClientLevel, Vec3, ParticleWizardry>> PROVIDERS = new LinkedHashMap<>();
    /// The fraction of the impact velocity that should be the maximum spread speed added on impact.
    private static final double SPREAD_FACTOR = 0.2;

    /// The fraction of the impact velocity that should be the lateral velocity reduced by.
    private static final double IMPACT_FRICTION = 0.2;

    /// Sets if the texture should be updated on tick. This is quite useful for particles that want to set a custom update or just
    /// have a static texture.
    private final boolean updateTextureOnTick;

    /// A long value used by the renderer as a random number seed, ensuring anything that is randomized remains the
    /// same across multiple frames
    protected long seed;

    /// A Random object used by the renderer to generate random numbers
    protected Random random = new Random();

    /// True if the particle is shaded, false if the particle always renders at full brightness
    protected boolean shaded = false;

    /// The initial color of the particle before any fade or color change
    protected float initialRed, initialGreen, initialBlue;

    /// The color fade of the particle, applied over time
    protected float fadeRed, fadeGreen, fadeBlue;

    /// The angle of rotation of the particle
    protected float angle;

    /// Radius of the particle, specially used for particles with spin
    protected double radius = 0;

    /// Speed of the particle, specially used for particles with spin
    protected double speed = 0;

    /// The entity this particle is linked to. (The particle will move with this entity)
    protected @Nullable Entity entity = null;

    /// Coordinates of this particle relative to the linked entity. If the linked entity is null, these are used as
    /// the absolute coordinates of the center of rotation for particles with spin. If the particle has neither a
    /// linked entity nor spin, these are not used.
    protected double relativeX, relativeY, relativeZ;

    /// Velocity of this particle relative to the linked entity. If the linked entity is null, these are not used.
    protected double relativeMotionX, relativeMotionY, relativeMotionZ;

    /// The yaw angle this particle is facing, or {@code NaN} if this particle always faces the viewer (default behavior).
    protected float yaw = Float.NaN;

    /// The pitch angle this particle is facing, or {@code NaN} if this particle always faces the viewer (default behavior).
    protected float pitch = Float.NaN;

    /// True if the particle should be adjusted in size based on distance from the camera.
    protected boolean adjustQuadSize;

    /// Sprites of the particle.
    SpriteSet spriteSet;

    /// Previous-tick velocity, used in collision detection.
    private double prevVelX, prevVelY, prevVelZ;

    private final Vector3f scratch = new Vector3f();

    public ParticleWizardry(ClientLevel world, double x, double y, double z, SpriteSet spriteSet, boolean updateTextureOnTick) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.relativeX = this.x;
        this.relativeY = this.y;
        this.relativeZ = this.z;
        this.updateTextureOnTick = updateTextureOnTick;
        this.setSpriteFromAge(spriteSet);
    }

    /// Sets the seed for this particle's randomly generated values and resets {@link ParticleWizardry#random} to use
    /// that seed. Implementations will differ between particle types; for example, ParticleLightning has an update
    /// period which changes the seed every few ticks, whereas ParticleVine simply retains the same seed for its entire
    /// lifetime.
    ///
    /// @param seed The seed to use
    public void setSeed(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    /// Sets whether the particle should render at full brightness or not. True if the particle is shaded, false if
    /// the particle always renders at full brightness. Defaults to false.
    ///
    /// @param shaded True if the particle should be shaded, false otherwise
    public void setShaded(boolean shaded) {
        this.shaded = shaded;
    }

    /// Sets this particle's gravity. True to enable gravity, false to disable. Defaults to false.
    ///
    /// @param gravity True to enable gravity, false otherwise
    public void setGravity(boolean gravity) {
        this.gravity = gravity ? 1 : 0;
    }

    /// Sets this particle's collisions. True to enable block collisions, false to disable. Defaults to false.
    ///
    /// @param canCollide True to enable block collisions, false otherwise
    public void setCollisions(boolean canCollide) {
        this.hasPhysics = canCollide;
    }

    /// Sets the spin parameters of the particle.
    ///
    /// @param radius The spin radius
    /// @param speed  The spin speed in rotations per tick
    public void setSpin(double radius, double speed) {
        this.radius = radius;
        this.speed = speed * 2 * Math.PI;
        this.angle = this.random.nextFloat() * (float) Math.PI * 2;
        this.x = relativeX - radius * Mth.cos(angle);
        this.z = relativeZ + radius * Mth.sin(angle);

        this.relativeMotionX = xd;
        this.relativeMotionY = yd;
        this.relativeMotionZ = zd;
    }

    /// Links this particle to the given entity. This will cause its position and velocity to be relative to the entity.
    ///
    /// @param entity The entity to link to.
    public void setEntity(Entity entity) {
        this.entity = entity;
        if (entity != null) {
            this.setPos(this.entity.xo + relativeX, this.entity.yo + relativeY, this.entity.zo + relativeZ);
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.relativeMotionX = xd;
            this.relativeMotionY = yd;
            this.relativeMotionZ = zd;
        }
    }


    /// Sets the base color of the particle. *Note that this also sets the fade color so that particles without a
    /// fade color do not change color at all; as such fade color must be set **after** calling this method.*
    ///
    /// @param red   The red color component
    /// @param green The green color component
    /// @param blue  The blue color component
    @Override
    public void setColor(float red, float green, float blue) {
        super.setColor(red, green, blue);
        initialRed = red;
        initialGreen = green;
        initialBlue = blue;
        // If fade color is not specified, it defaults to the main color - this method is always called first
        setFadeColor(red, green, blue);
    }

    /// Sets the fade color of the particle.
    ///
    /// @param r The red color component
    /// @param g The green color component
    /// @param b The blue color component
    public void setFadeColor(float r, float g, float b) {
        this.fadeRed = r;
        this.fadeGreen = g;
        this.fadeBlue = b;
    }

    /// Sets the direction this particle faces. This will cause the particle to render facing the given direction.
    ///
    /// @param yaw   The yaw angle of this particle in degrees, where 0 is south.
    /// @param pitch The pitch angle of this particle in degrees, where 0 is horizontal.
    public void setFacing(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float f) {
        return shaded ? super.getLightColor(f) : 15728880;
    }

    @Override
    public void render(@NotNull VertexConsumer vertexConsumer, @NotNull Camera camera, float tickDelta) {
        updateEntityLinking(tickDelta);

        Quaternionf rotation;

        if (Float.isNaN(this.yaw) || Float.isNaN(this.pitch)) {
            rotation = camera.rotation(); // Default: billboard to face the camera, same as vanilla
        } else {
            rotation = new Quaternionf().rotateY((float) Math.toRadians(-this.yaw)).rotateX((float) Math.toRadians(this.pitch));
        }

        drawParticle(vertexConsumer, camera, tickDelta, rotation);
    }


    protected void drawParticle(VertexConsumer buffer, Camera camera, float partialTicks, Quaternionf rotation) {
        Vec3 camPos = camera.getPosition();

        float scale = this.adjustQuadSize ? 0.1f : 1;
        float size = scale * this.getQuadSize(partialTicks);

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        int light = this.getLightColor(partialTicks);

        for (int i = 0; i < 4; i++) {
            float u, v;
            switch (i) {
                case 0 -> {
                    scratch.set(-1, -1, 0);
                    u = u1;
                    v = v1;
                }
                case 1 -> {
                    scratch.set(-1, 1, 0);
                    u = u1;
                    v = v0;
                }
                case 2 -> {
                    scratch.set(1, 1, 0);
                    u = u0;
                    v = v0;
                }
                default -> {
                    scratch.set(1, -1, 0);
                    u = u0;
                    v = v1;
                }
            }
            scratch.rotate(rotation);
            scratch.mul(size);
            scratch.add(x, y, z);
            buffer.vertex(scratch.x, scratch.y, scratch.z)
                    .uv(u, v)
                    .color(this.rCol, this.gCol, this.bCol, this.alpha)
                    .uv2(light)
                    .endVertex();
        }
    }



    protected void updateEntityLinking(float partialTicks) {
        if (this.entity == null) return;
        double entityX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double entityY = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double entityZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        x = entityX + relativeX;
        y = entityY + relativeY;
        z = entityZ + relativeZ;
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
                    return;
                } else {
                    this.relativeX += relativeMotionX;
                    this.relativeY += relativeMotionY;
                    this.relativeZ += relativeMotionZ;

                    x = this.entity.getX() + relativeX;
                    y = this.entity.getY() + relativeY;
                    z = this.entity.getZ() + relativeZ;
                }
            } else {
                this.relativeX += relativeMotionX;
                this.relativeY += relativeMotionY;
                this.relativeZ += relativeMotionZ;
            }

            if (radius > 0) {
                angle += (float) speed;
                x += radius * -Mth.cos(angle);
                z += radius * Mth.sin(angle);
            }

            this.setPos(x, y, z);
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
        }

        this.prevVelX = xd;
        this.prevVelY = yd;
        this.prevVelZ = zd;

        if (updateTextureOnTick) {
            this.setSpriteFromAge(spriteSet);
        }
    }

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

        if (d0 != dx) this.xd = 0.0D;
        if (d1 != dy) this.yd = 0.0D;
        if (d2 != dz) this.zd = 0.0D;
    }
}