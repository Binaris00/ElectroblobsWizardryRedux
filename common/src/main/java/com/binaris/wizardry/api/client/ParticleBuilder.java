package com.binaris.wizardry.api.client;

import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.api.client.particle.ParticleWizardry;
import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.client.ParticleSpawner;
import com.binaris.wizardry.core.networking.s2c.ParticleBuilderS2C;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


/// Fluent builder for creating and spawning custom wizardry particles on the client.
///
/// This is a stateful singleton — only one particle can be configured at a time. Callers start a build
/// with one of the static {@code create()} overloads, chain setter methods to configure position, velocity,
/// color, lifetime, spin, collision, and targeting, then finish with {@code spawn(world)}. Server-side
/// spawning is normally blocked (particles are client-only) unless {@code allowServer(true)} is set,
/// which sends a {@code ParticleBuilderS2C} packet to all clients in the dimension. After each spawn,
/// all builder state is reset to defaults.
///
/// @see ParticleWizardry
/// @see ParticleSpawner
public final class ParticleBuilder {
    /// Singleton instance
    private static final ParticleBuilder instance = new ParticleBuilder();
    /// The particle type
    private SimpleParticleType particle;
    /// If the particle is building, if not, always throw an error
    private boolean building;
    /// The lifetime of the particle
    private int lifetime;
    /// The scale of the particle, perfect to set bigger particles...
    private float scale;
    /// The position of the particle
    private double x, y, z;
    /// The color of the particle
    private float red, green, blue;
    /// The velocity of the particle
    private double velocityX, velocityY, velocityZ;
    /// The fade color of the particle
    private float fadeRed, fadeGreen, fadeBlue;
    /// The shaded property of the particle, false by default
    private boolean shaded;
    /// The gravity property of the particle, false by default
    private boolean gravity;
    /// Normally we don't want to allow server-side spawning of particles, as this can cause performance issues (too many
    /// particles being sent to clients). However, in some cases (notably for particles that aren't from spells), it is
    /// desirable to allow this. This flag indicates whether server-side spawning is allowed for the particle being built.
    ///
    /// There's no need to allow this for spell casting (normally) as spells handle particle spawning and synchronization
    /// themselves, check [Spell#requiresPacket()] for more details.
    private boolean serverAllowed;
    /// The random seed for particle variation; determines procedural texture offsets.
    private long seed;
    /// The length of the particle for beam-like particles (e.g. BEAM, LIGHTNING).
    private double length;
    /// The entity the particle follows; if set, position coordinates become relative to this entity.
    private Entity entity;
    /// The rotation of the particle in degrees; yaw=0 faces south, pitch=0 is horizontal.
    private float yaw, pitch;
    /// The spin radius for the particle; how far it orbits from its base position.
    private double radius;
    /// The spin speed in revolutions per tick.
    private double rpt;
    /// Whether the particle checks for and reacts to block collisions.
    private boolean collide;
    /// The target position the particle stretches toward (for beam/lightning types).
    private double tx, ty, tz;
    /// The velocity of the target point, causing the beam/lightning endpoint to move over time.
    private double tvx, tvy, tvz;
    /// The target entity the particle stretches toward (for beam/lightning types).
    private Entity target;

    // ------------------------- Core methods -------------------------------- //

    /// Start building a particle.
    /// This is just for more readable code with a static function.
    ///
    /// @param particle The particle type
    /// @return The ParticleBuilder instance
    /// @throws IllegalStateException If already building
    /// @see #particle(DeferredObject)
    public static ParticleBuilder create(DeferredObject<SimpleParticleType> particle) {
        return ParticleBuilder.instance.particle(particle);
    }

    /// Starts building a particle of the given type and positions it randomly within the given entity's bounding box.
    /// Equivalent to calling `ParticleBuilder.create(type).pos(...)`; users should chain any additional builder
    /// methods onto this one and finish with `.spawn(world)` as normal.
    /// Used extensively with summoned creatures; makes code much neater and more concise.
    ///
    /// _N.B. this does **not** cause the particle to move with the given entity._
    ///
    /// @param type   The type of particle to build
    /// @param entity The entity to position the particle at
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is already building.
    public static ParticleBuilder create(DeferredObject<SimpleParticleType> type, Entity entity) {

        double x = entity.xo + (entity.level().random.nextDouble() - 0.5D) * (double) entity.getBbWidth();
        double y = entity.yo + entity.level().random.nextDouble() * (double) entity.getBbWidth();
        double z = entity.zo + (entity.level().random.nextDouble() - 0.5D) * (double) entity.getBbWidth();

        return ParticleBuilder.instance.particle(type).pos(x, y, z);
    }

    /// Creates a particle at a random position within a radius of the given position.
    /// Just in case if you need to spawn random particles in a radius.
    /// For creating a normal particle, use [#create(DeferredObject)]
    ///
    /// @param type   The particle type
    /// @param random The random object
    /// @param x      The x position
    /// @param y      The y position
    /// @param z      The z position
    /// @param radius The radius
    public static ParticleBuilder create(DeferredObject<SimpleParticleType> type, RandomSource random, double x, double y, double z, double radius) {
        double px = x + (random.nextDouble() * 2 - 1) * radius;
        double py = y + (random.nextDouble() * 2 - 1) * radius;
        double pz = z + (random.nextDouble() * 2 - 1) * radius;

        return ParticleBuilder.create(type).pos(px, py, pz);
    }

    /// Starts building a particle of the given type and positions it randomly within the given radius of the given position,
    /// with velocity proportional to distance from the given position if move is true. Good for making explosion-type effects.
    /// Equivalent to calling `ParticleBuilder.create(type).pos(...).vel(...)`; users should chain any additional builder
    /// methods onto this one and finish with `.spawn(world)` as normal.
    ///
    /// @param type   The type of particle to build
    /// @param random An RNG instance
    /// @param x      The x coordinate of the center of the region in which to position the particle
    /// @param y      The y coordinate of the center of the region in which to position the particle
    /// @param z      The z coordinate of the center of the region in which to position the particle
    /// @param radius The radius of the region in which to position the particle
    /// @param move   Whether the particle should move outwards from the center (note that if this is false, the particle's
    ///               default velocity will apply)
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is already building.
    public static ParticleBuilder create(DeferredObject<SimpleParticleType> type, RandomSource random, double x, double y, double z, double radius, boolean move) {
        double px = x + (random.nextDouble() * 2 - 1) * radius;
        double py = y + (random.nextDouble() * 2 - 1) * radius;
        double pz = z + (random.nextDouble() * 2 - 1) * radius;

        if (move) {
            return ParticleBuilder.create(type).pos(px, py, pz).velocity(px - x, py - y, pz - z);
        }

        return ParticleBuilder.create(type).pos(px, py, pz);
    }

    // ------------------------- Helper methods -------------------------------- //

    /// Spawns electrical spark and smoke particles at the given position, used for lightning and shock effects.
    ///
    /// Creates 8 randomly-placed spark particles and 8 large smoke particles within a 1-block radius
    /// of the given coordinates. Used internally by lightning spells and shock-type effects.
    ///
    /// @param world the world to spawn particles in.
    /// @param x     the center x-coordinate.
    /// @param y     the center y-coordinate.
    /// @param z     the center z-coordinate.
    public static void spawnShockParticles(Level world, double x, double y, double z) {
        double px, py, pz;

        for (int i = 0; i < 8; i++) {
            px = x + world.random.nextDouble() - 0.5;
            py = y + world.random.nextDouble() - 0.5;
            pz = z + world.random.nextDouble() - 0.5;
            ParticleBuilder.create(EBParticles.SPARK).pos(px, py, pz).spawn(world);

            px = x + world.random.nextDouble() - 0.5;
            py = y + world.random.nextDouble() - 0.5;
            pz = z + world.random.nextDouble() - 0.5;
            world.addParticle(ParticleTypes.LARGE_SMOKE, px, py, pz, 0, 0, 0);
        }
    }

    /// Spawns healing sparkle particles around an entity, plus a buff indicator particle.
    ///
    /// Creates 10 yellow sparkle particles with mild upward velocity randomly positioned around
    /// the entity's upper body, then places a single buff particle directly on the entity. Used
    /// by healing spells and recovery effects.
    ///
    /// @param world  the world to spawn particles in.
    /// @param entity the entity to center the particles around.
    public static void spawnHealParticles(Level world, LivingEntity entity) {
        for (int i = 0; i < 10; i++) {
            double x = entity.getX() + world.random.nextDouble() * 2 - 1;
            double y = entity.getY() + entity.getDimensions(entity.getPose()).height * 0.85F - 0.5 + world.random.nextDouble();
            double z = entity.getZ() + world.random.nextDouble() * 2 - 1;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.1, 0).color(1, 1, 0.3f).spawn(world);
        }

        ParticleBuilder.create(EBParticles.BUFF).entity(entity).color(1, 1, 0.3f).spawn(world);
    }

    // ------------------------- Setters -------------------------------- //

    /// Start building a particle. For creating a particle in a static way use [#create(DeferredObject)]
    ///
    /// @param particle The particle type
    /// @return The ParticleBuilder instance
    private ParticleBuilder particle(DeferredObject<SimpleParticleType> particle) {
        if (instance.building) {
            EBLogger.warn("Attempted to build an already built particle: " + this.getCurrentParticleString());
            return instance;
        }
        this.particle = particle.get();
        this.building = true;
        return this;
    }

    /// Gets a readable string representation of the current builder parameters; used in error messages.
    private String getCurrentParticleString() {
        return String.format("[ Type: %s, Position: (%s, %s, %s), Velocity: (%s, %s, %s), Colour: (%s, %s, %s), "
                        + "Fade Colour: (%s, %s, %s), Radius: %s, Revs/tick: %s, Lifetime: %s, Gravity: %s, Shaded: %s, "
                        + "Scale: %s, Entity: %s ]",
                particle, x, y, z, velocityX, velocityY, velocityZ, red, green, blue, fadeRed, fadeGreen, fadeBlue, radius, rpt, lifetime, gravity, shaded, scale, entity);
    }

    /// Sets the position of the particle.
    ///
    /// @param x The x position
    /// @param y The y position
    /// @param z The z position
    /// @throws IllegalStateException If not building yet
    public ParticleBuilder pos(double x, double y, double z) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    /// Sets the position of the particle.
    ///
    /// @param pos The position
    /// @throws IllegalStateException If not building yet
    public ParticleBuilder pos(BlockPos pos) {
        return this.pos(pos.getX(), pos.getY(), pos.getZ());
    }

    /// Sets the position of the particle.
    ///
    /// @param vec3d The position
    /// @throws IllegalStateException If not building yet
    public ParticleBuilder pos(Vec3 vec3d) {
        return this.pos(vec3d.x(), vec3d.y(), vec3d.z());
    }

    /// Set the max age of the particle.
    ///
    /// @param lifetime The lifetime
    /// @throws IllegalStateException If not building yet
    public ParticleBuilder time(int lifetime) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.lifetime = lifetime;
        return this;
    }

    /// Sets the velocity of the particle.
    ///
    /// @param velocityX The x velocity
    /// @param velocityY The y velocity
    /// @param velocityZ The z velocity
    /// @throws IllegalStateException If not building yet
    public ParticleBuilder velocity(double velocityX, double velocityY, double velocityZ) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        return this;
    }

    /// Sets the velocity of the particle being built.
    /// This is a vector-based alternative to [ParticleBuilder#velocity(double, double, double)] (
    /// double, double, double)}, allowing for even more concise code when a vector is available.
    ///
    /// **Affects:** All particle types
    ///
    /// @param vel A vector representing the velocity of the particle to be built.
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder velocity(Vec3 vel) {
        return velocity(vel.x, vel.y, vel.z);
    }

    /// Set the scale of the particle
    ///
    /// @param scale The scale
    /// @throws IllegalStateException If not building yet
    public ParticleBuilder scale(float scale) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.scale = scale;
        return this;
    }

    /// Sets the color of the particle being built. If unspecified, this defaults to the particle's default color,
    /// specified within its constructor. _If all color components are 0 or 1, at least one must have the float suffix
    /// (`f` or `F`) or the integer overload will be used instead, causing the particle to appear black!_
    ///
    /// **Affects:** All particle types except [EBParticles#ICE], [EBParticles#MAGIC_BUBBLE]
    /// and [EBParticles#MAGIC_FIRE]
    ///
    /// @param r The red color component to set; will be clamped to between zero and one
    /// @param g The green color component to set; will be clamped to between zero and one
    /// @param b The blue color component to set; will be clamped to between zero and one
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder color(float r, float g, float b) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.red = Mth.clamp(r, 0, 1);
        this.green = Mth.clamp(g, 0, 1);
        this.blue = Mth.clamp(b, 0, 1);
        return this;
    }

    /// Sets the color of the particle being built.
    /// This is an 8-bit (0-255) integer version of
    /// [ParticleBuilder#color(float, float, float)].
    ///
    /// **Affects:** All particle types except [EBParticles#ICE], [EBParticles#MAGIC_BUBBLE]
    /// and [EBParticles#MAGIC_FIRE]
    ///
    /// @param r The red color component to set; will be clamped to between 0 and 255
    /// @param g The green color component to set; will be clamped to between 0 and 255
    /// @param b The blue color component to set; will be clamped to between 0 and 255
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder color(int r, int g, int b) {
        return this.color(r / 255f, g / 255f, b / 255f);
    }

    /// Sets the color of the particle being built. This is a 6-digit hex color version of
    /// [ParticleBuilder#color(float, float, float)].
    ///
    /// **Affects:** All particle types except [EBParticles#ICE], [EBParticles#MAGIC_BUBBLE]
    /// and [EBParticles#MAGIC_FIRE]
    ///
    /// @param hex The color to be set, as a packed 6-digit hex integer (e.g. 0xff0000).
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder color(int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);
        return this.color(r, g, b);
    }

    /// Sets the fade color of the particle being built.
    /// If unspecified, this defaults to whatever the particle's base
    /// color is. _If all color components are 0 or 1, at least one must have the float suffix
    /// (`f` or `F`) or the integer overload will be used instead, causing the particle to appear black!_
    ///
    /// **Affects:** All particle types except [EBParticles#ICE], [EBParticles#MAGIC_BUBBLE]
    /// and [EBParticles#MAGIC_FIRE]
    ///
    /// @param r The red color component to set; will be clamped to between zero and one
    /// @param g The green color component to set; will be clamped to between zero and one
    /// @param b The blue color component to set; will be clamped to between zero and one
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder fade(float r, float g, float b) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.fadeRed = Mth.clamp(r, 0, 1);
        this.fadeGreen = Mth.clamp(g, 0, 1);
        this.fadeBlue = Mth.clamp(b, 0, 1);
        return this;
    }

    /// Sets the fade color of the particle being built. This is an 8-bit (0-255) integer version of
    /// [ParticleBuilder#fade(float, float, float)].
    ///
    /// **Affects:** All particle types except [EBParticles#ICE], [EBParticles#MAGIC_BUBBLE]
    /// and [EBParticles#MAGIC_FIRE]
    ///
    /// @param r The red color component to set; will be clamped to between 0 and 255
    /// @param g The green color component to set; will be clamped to between 0 and 255
    /// @param b The blue color component to set; will be clamped to between 0 and 255
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder fade(int r, int g, int b) {
        return this.fade(r / 255f, g / 255f, b / 255f); // Yes, 255 is correct and not 256, or else we can't have pure white
    }

    /// Sets the fade color of the particle being built. This is a 6-digit hex color version of
    /// [ParticleBuilder#fade(float, float, float)].
    ///
    /// **Affects:** All particle types except [EBParticles#ICE], [EBParticles#MAGIC_BUBBLE]
    /// and [EBParticles#MAGIC_FIRE]
    ///
    /// @param hex The color to be set, as a packed 6-digit hex integer (e.g., 0xff0000).
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder fade(int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);
        return this.fade(r, g, b);
    }

    /// Sets the seed of the particle being built. If unspecified, this defaults to the particle's default seed,
    /// specified within its constructor (this is normally chosen at random).
    ///
    /// _Pro tip: to get a particle to stay the same while a continuous spell is in use (but change between casts),
    /// use `.seed(world.getTotalWorldTime() - ticksInUse)`._
    ///
    /// **Affects:** All particle types
    ///
    /// @param seed The seed to set
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder seed(long seed) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.seed = seed;
        return this;
    }

    /// Sets the spin parameters of the particle being built.
    /// If unspecified, these both default to 0.
    ///
    /// **Affects:** All particle types
    ///
    /// @param radius The rotation radius to set
    /// @param speed  The rotation speed to set, in revolutions per tick
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder spin(double radius, double speed) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.radius = radius;
        this.rpt = speed;
        return this;
    }

    /// Sets the collisions of the particle being built.
    /// If unspecified, this defaults to false.
    ///
    /// **Affects:** All particle types
    ///
    /// @param collide True to enable block collisions for the particle, false to disable
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder collide(boolean collide) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.collide = collide;
        return this;
    }

    /// Sets the entity of the particle being built.
    /// This will cause the particle to move with the given entity, and will
    /// make the position specified
    /// using [ParticleBuilder#pos(double, double, double)] _relative to_ that
    /// entity's position.
    ///
    /// **Affects:** All particle types
    ///
    /// @param entity The entity to set (passing in null will do nothing but will not cause any problems, so for the sake
    ///               of conciseness it is not necessary to perform a null check on the passed-in argument)
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder entity(Entity entity) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.entity = entity;
        return this;
    }

    /// Sets the rotation of the particle being built.
    /// If unspecified, the particle will use the default behavior and
    /// rotate to face the viewer.
    ///
    /// **Affects:** All particle types
    ///
    /// @param yaw   The yaw angle to set in degrees, where 0 is south.
    /// @param pitch The pitch angle to set in degrees, where 0 is horizontal.
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder face(float yaw, float pitch) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.yaw = yaw;
        this.pitch = pitch;
        return this;
    }

    /// Sets the rotation of the particle being built. This is an `EnumFacing`-based alternative to
    /// [ParticleBuilder#face(float, float)] which sets the yaw and pitch to the appropriate angles for the given facing.
    /// For example, if the given facing is `NORTH`, the particle will render parallel to the north face of blocks.
    /// If unspecified, the particle will use the default behavior and rotate to face the viewer.
    ///
    /// **Affects:** All particle types
    ///
    /// @param direction The `EnumFacing` direction to set.
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder face(Direction direction) {
        return face(direction.toYRot(), direction.getAxis().isVertical() ? direction.getAxisDirection().getStep() * 90 : 0);
    }

    /// Sets whether server-side spawning is allowed for the particle being built. By default this is false.
    ///
    /// There's no need to allow this for spell casting (normally) as spells handle particle spawning and synchronization
    /// themselves, check [Spell#requiresPacket()] for more details.
    ///
    /// @param allow True to allow server-side spawning, false to prevent it
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder allowServer(boolean allow) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.serverAllowed = allow;
        return this;
    }

    // ============================================= Targeted-only methods =============================================

    /// Set the shaded property of the particle.
    ///
    /// @param value The value
    /// @throws IllegalStateException If not building yet
    public ParticleBuilder shaded(boolean value) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.shaded = value;
        return this;
    }

    /// Set the gravity property of the particle.
    ///
    /// @param value The value
    /// @throws IllegalStateException If not building yet
    public ParticleBuilder gravity(boolean value) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.gravity = value;
        return this;
    }

    /// Sets the target of the particle being built. This will cause the particle to stretch to touch the given position.
    ///
    /// **Affects:** Targeted particles, namely [EBParticles#BEAM], [EBParticles#LIGHTNING] and
    ///
    /// @param x The target x-coordinate to set
    /// @param y The target y-coordinate to set
    /// @param z The target z-coordinate to set
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder target(double x, double y, double z) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.tx = x;
        this.ty = y;
        this.tz = z;
        return this;
    }

    /// Sets the target of the particle being built. This is a vector-based alternative to
    /// [ParticleBuilder#target(double, double, double)], allowing for even more concise code when a vector is
    /// available.
    ///
    /// **Affects:** Targeted particles, namely [EBParticles#BEAM], [EBParticles#LIGHTNING] and
    ///
    /// @param pos A vector representing the target position of the particle to be built.
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder target(Vec3 pos) {
        return target(pos.x, pos.y, pos.z);
    }

    /// Sets the target point velocity of the particle being built. This will cause the position it stretches to touch to move
    /// at the given velocity. Has no effect unless [ParticleBuilder#target(double, double, double)] or one of its
    /// overloads is also set.
    ///
    /// **Affects:** Targeted particles, namely [EBParticles#BEAM], [EBParticles#LIGHTNING] and
    ///
    /// @param vx The target point x velocity to set
    /// @param vy The target point y velocity to set
    /// @param vz The target point z velocity to set
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder tvel(double vx, double vy, double vz) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.tvx = vx;
        this.tvy = vy;
        this.tvz = vz;
        return this;
    }

    /// Sets the target point velocity of the particle being built. This is a vector-based alternative to
    /// [ParticleBuilder#tvel(double, double, double)], allowing for even more concise code when a vector is
    /// available.
    ///
    /// **Affects:** Targeted particles, namely [EBParticles#BEAM], [EBParticles#LIGHTNING] and
    ///
    /// @param vel A vector representing the target point velocity of the particle to be built.
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder tvel(Vec3 vel) {
        return tvel(vel.x, vel.y, vel.z);
    }

    /// Sets the target and target velocity of the particle being built. This method takes an origin entity and a
    /// position and estimates the position of the target point based on the given entity's rotational velocities and its
    /// distance from the given position.
    ///
    /// **Affects:** Targeted particles, namely [EBParticles#BEAM], [EBParticles#LIGHTNING] and
    ///
    /// @param length The length of the particle being built.
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder length(double length) {
        this.length = length;
        return this;
    }

    /// Sets the target of the particle being built. This will cause the particle to stretch to touch the given entity.
    ///
    /// **Affects:** Targeted particles, namely [EBParticles#BEAM], [EBParticles#LIGHTNING] and
    ///
    /// @param target The entity to set
    /// @return The particle builder instance, allowing other methods to be chained onto this one
    /// @throws IllegalStateException if the particle builder is not yet building.
    public ParticleBuilder target(Entity target) {
        if (!building) throw new IllegalStateException("Not building yet!");
        this.target = target;
        return this;
    }

    /// Spawn the particle in the world.
    ///
    /// Use [ParticleWizardry#PROVIDERS] to get the particle factory,
    /// set the properties and then add the particle to the particle manager.
    ///
    /// Warn if the particle is being spawned at (0, 0, 0)
    /// and the entity is null or ParticleBuilder is being used in server side,
    /// as this is likely to be a mistake.
    ///
    /// @param world The world
    /// @throws IllegalStateException If not building yet
    public void spawn(Level world) {
        if (!building) throw new IllegalStateException("Not building yet!");

        // Error checking
        if (x == 0 && y == 0 && z == 0 && entity == null)
            EBLogger.error("Failed to spawn particle of type - %s - are you sure it exists?");

        // Server-side spawning: send packet to clients
        if (serverAllowed && !world.isClientSide()) {
            ParticleData data = captureData();
            ParticleBuilderS2C packet = new ParticleBuilderS2C(data);
            Services.NETWORK_HELPER.sendToDimension(world.getServer(), packet, world.dimension());
            reset();
            return;
        }

        if (!world.isClientSide()) {
            EBLogger.error("ParticleBuilder.spawn(...) called on the server side! ParticleBuilder has prevented a server crash, but calling it on the server will do nothing. Consider adding a world.isClientSide() check or use .serverAllowed(true) to send particles to clients.");
            reset();
            return;
        }

        ParticleSpawner.spawnClientParticle(captureData());
        reset();
    }


    /// Packages all current builder state into a {@code ParticleData} DTO for network sync or client-side spawning.
    ///
    /// Reads every configured field — position, velocity, colors, lifetime, spin, collision, targeting, entity IDs —
    /// and populates a serializable data object. Called internally by {@code spawn()} before sending the packet
    /// (server-side) or handing data to {@code ParticleSpawner} (client-side).
    ///
    /// @return a fully-populated snapshot of the current builder configuration.
    private ParticleData captureData() {
        ParticleData data = new ParticleData();
        data.particleType = BuiltInRegistries.PARTICLE_TYPE.getKey(particle);
        data.x = x;
        data.y = y;
        data.z = z;
        data.vx = velocityX;
        data.vy = velocityY;
        data.vz = velocityZ;
        data.r = red;
        data.g = green;
        data.b = blue;
        data.fr = fadeRed;
        data.fg = fadeGreen;
        data.fb = fadeBlue;
        data.lifetime = lifetime;
        data.scale = scale;
        data.gravity = gravity;
        data.shaded = shaded;
        data.collide = collide;
        data.radius = radius;
        data.rpt = rpt;
        data.yaw = yaw;
        data.pitch = pitch;
        data.seed = seed;
        data.length = length;
        data.tx = tx;
        data.ty = ty;
        data.tz = tz;
        data.tvx = tvx;
        data.tvy = tvy;
        data.tvz = tvz;
        data.entityId = entity != null ? entity.getId() : null;
        data.targetId = target != null ? target.getId() : null;
        return data;
    }

    /// reset all the properties to the default values
    private void reset() {
        building = false;
        particle = null;
        x = 0;
        y = 0;
        z = 0;
        velocityX = Double.NaN;
        velocityY = Double.NaN;
        velocityZ = Double.NaN;
        red = -1;
        green = -1;
        blue = -1;
        fadeRed = -1;
        fadeGreen = -1;
        fadeBlue = -1;
        radius = 0;
        rpt = 0;
        lifetime = -1;
        gravity = false;
        shaded = false;
        collide = false;
        scale = 1;
        entity = null;
        yaw = Float.NaN;
        pitch = Float.NaN;
        tx = Double.NaN;
        ty = Double.NaN;
        tz = Double.NaN;
        tvx = Double.NaN;
        tvy = Double.NaN;
        tvz = Double.NaN;
        target = null;
        seed = 0;
        length = -1;
        serverAllowed = false;
    }

    /// Data class for particle properties to be sent over the network and spawned client-side. In the normal course of
    /// events, users of ParticleBuilder will not need to interact with this class directly, as ParticleBuilder handles
    /// it internally.
    public static class ParticleData {
        public ResourceLocation particleType;
        public double x, y, z;
        public double vx, vy, vz;
        public float r, g, b;
        public float fr, fg, fb;
        public int lifetime;
        public float scale;
        public boolean gravity, shaded, collide;
        public double radius;
        public double rpt;
        public float yaw, pitch;
        public long seed;
        public double length;
        public double tx, ty, tz;
        public double tvx, tvy, tvz;
        public Integer entityId;
        public Integer targetId;

        /// Deserializes a {@code ParticleData} from a network buffer.
        ///
        /// Reads all fields in the exact order they were written by {@code write()}, reconstructing
        /// the complete particle configuration received from the server.
        ///
        /// @param buf the packet buffer to read from.
        ///
        /// @return a fully-populated {@code ParticleData} instance.
        public static ParticleData read(FriendlyByteBuf buf) {
            ParticleData data = new ParticleData();
            data.particleType = buf.readResourceLocation();
            data.x = buf.readDouble();
            data.y = buf.readDouble();
            data.z = buf.readDouble();
            data.vx = buf.readDouble();
            data.vy = buf.readDouble();
            data.vz = buf.readDouble();
            data.r = buf.readFloat();
            data.g = buf.readFloat();
            data.b = buf.readFloat();
            data.fr = buf.readFloat();
            data.fg = buf.readFloat();
            data.fb = buf.readFloat();
            data.lifetime = buf.readInt();
            data.scale = buf.readFloat();
            data.gravity = buf.readBoolean();
            data.shaded = buf.readBoolean();
            data.collide = buf.readBoolean();
            data.radius = buf.readDouble();
            data.rpt = buf.readDouble();
            data.yaw = buf.readFloat();
            data.pitch = buf.readFloat();
            data.seed = buf.readLong();
            data.length = buf.readDouble();
            data.tx = buf.readDouble();
            data.ty = buf.readDouble();
            data.tz = buf.readDouble();
            data.tvx = buf.readDouble();
            data.tvy = buf.readDouble();
            data.tvz = buf.readDouble();
            if (buf.readBoolean()) data.entityId = buf.readInt();
            if (buf.readBoolean()) data.targetId = buf.readInt();
            return data;
        }

        /// Serializes this {@code ParticleData} to a network buffer.
        ///
        /// Writes all fields in a fixed order matching {@code read()}, including nullable entity and target
        /// IDs as optional booleans followed by the integer value if present.
        ///
        /// @param buf the packet buffer to write to.
        public void write(FriendlyByteBuf buf) {
            buf.writeResourceLocation(particleType);
            buf.writeDouble(x);
            buf.writeDouble(y);
            buf.writeDouble(z);
            buf.writeDouble(vx);
            buf.writeDouble(vy);
            buf.writeDouble(vz);
            buf.writeFloat(r);
            buf.writeFloat(g);
            buf.writeFloat(b);
            buf.writeFloat(fr);
            buf.writeFloat(fg);
            buf.writeFloat(fb);
            buf.writeInt(lifetime);
            buf.writeFloat(scale);
            buf.writeBoolean(gravity);
            buf.writeBoolean(shaded);
            buf.writeBoolean(collide);
            buf.writeDouble(radius);
            buf.writeDouble(rpt);
            buf.writeFloat(yaw);
            buf.writeFloat(pitch);
            buf.writeLong(seed);
            buf.writeDouble(length);
            buf.writeDouble(tx);
            buf.writeDouble(ty);
            buf.writeDouble(tz);
            buf.writeDouble(tvx);
            buf.writeDouble(tvy);
            buf.writeDouble(tvz);
            buf.writeBoolean(entityId != null);
            if (entityId != null) buf.writeInt(entityId);
            buf.writeBoolean(targetId != null);
            if (targetId != null) buf.writeInt(targetId);
        }
    }
}