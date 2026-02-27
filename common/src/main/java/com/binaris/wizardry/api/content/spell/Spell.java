package com.binaris.wizardry.api.content.spell;

import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.LocationCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.content.spell.abstr.ArrowSpell;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.content.spell.ice.FrostRay;
import com.binaris.wizardry.core.ClientSpellSoundManager;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.binaris.wizardry.core.ClientSpellSoundManager.playSpellSoundLoop;


/**
 * Abstract blueprint for every spell in the mod.
 * <p>
 * Spell is the high-level contract that all concrete spell classes must follow. It defines the casting entry points, a
 * small sound helper API, and access to data-driven properties that configure a spell's behavior. Implementations usually
 * override one or more cast(...) methods and the properties() factory to provide defaults and custom behavior.
 * <br>
 * Responsibilities: <p>
 * - Provide cast(...) entry points for player, entity or location-based casts. <p>
 * - Expose configurable data via SpellProperties and the helper property(...). <p>
 * - Resolve name/description/icon from the registry using Services.REGISTRY_UTIL. <p>
 * - Offer simple sound helpers for one-shot and continuous sounds. <p>
 * <br>
 * Recommended implementation pattern: <p>
 * 1) Override properties() to set defaults (cost, cooldown, element, tier, ...). <p>
 * 2) Override the appropriate cast(...) method for your source (player/entity/location). If entities or locations are
 * valid casters, enable them via canCastByEntity() / canCastByLocation(). <p>
 * 3) Use playSound(...) or playSoundLoop(...) for audio. Continuous spells should use playSoundLoop(...) to start the
 * client-side loop at the first tick. <p>
 * 4) For small behaviors, consider using existing abstract helpers such as RaySpell or ArrowSpell instead of a custom
 * class. <p>
 * <br>
 * Client/server notes: <p>
 * By default, spells are processed on the server and mirrored to clients when requiresPacket() returns true. Keep
 * requiresPacket() as true if your spell spawns particles or relies on client-side effects. Override only as an optimization
 * when logic is purely server-side. <p>
 * <br>
 * Edge cases and notes: <p>
 * - Equality is based on the registered ResourceLocation; two spell instances with the same registry key are considered
 * equal. <p>
 * - Default continuous sound names are derived from the spell path. The standard loop expects three sounds:
 * spell.<namespace>.<path>.start, .loop and .end. <p>
 * - Properties are assigned from properties() in the constructor, but can be replaced at runtime with assignProperties(...)
 * for dynamic or test variants. <p>
 */
public abstract class Spell {
    /** The volume of the sound played by this spell, relative to 1. */
    protected float volume = 1;
    /** The pitch of the sound played by this spell, relative to 1. */
    protected float pitch = 1;
    /** The random variation in the pitch of the sound played by this spell. */
    protected float pitchVariation = 0;
    /** Description ID is how a spell is formatted, e.g. "spell.ebwizardry.fireball" */
    private String descriptionId;
    /** Location is where the spell is registered, e.g. "ebwizardry:fireball" */
    private ResourceLocation location;
    /** Icon is the resource location of the spell's icon, by default: "textures/spells/[namespace]/[path].png" */
    private ResourceLocation icon;
    /** The properties of this spell, loaded from the data files. */
    private SpellProperties properties = SpellProperties.empty();

    public Spell() {
        this.properties = properties();
    }

    // ===================================================
    // CASTING
    // ===================================================

    /**
     * This cast method is meant to be used for spells that are cast by a player source. This is useful for spells that
     * are meant to be cast by players, as it provides more information about the caster and the context of the cast.
     * <p>
     * Override this method to implement the casting behavior for spells that are meant to be cast by players.
     *
     * @param ctx The context of the spell cast, containing information about the world, caster, hand used, modifiers, etc.
     * @return true if the spell was successfully cast, false otherwise. If this returns false, the spell will not be
     * considered as having been cast, so no cooldown will be applied.
     */
    public abstract boolean cast(PlayerCastContext ctx);

    /**
     * This cast method is meant to be used for spells that are cast by an entity source, like a mob. This is
     * useful for spells that are meant to be cast by entities, as it provides more information about the caster and the
     * context of the cast.
     * <p>
     * Override this method to implement the casting behavior for spells that are meant to be cast by entities.
     *
     * @param ctx The context of the spell cast, containing information about the world, caster, modifiers, etc.
     * @return true if the spell was successfully cast, false otherwise. If this returns false, the spell will not be
     * considered as having been cast, so no cooldown will be applied.
     */
    public boolean cast(EntityCastContext ctx) {
        return false;
    }

    /**
     * This cast method is meant to be used for spells that are not cast by an entity, but rather by a non-entity source,
     * like a command block or a dispenser. This is useful for spells that are meant to be cast in a specific location
     * without needing an entity to cast them. By default, this returns false, as most spells are meant to be cast by
     * entities. You can override this to return true if your spell is meant to be cast by non-entity sources and to implement
     * the casting behavior for that case.
     *
     * @param ctx The context of the spell cast, containing information about the world, location, modifiers, etc.
     * @return true if the spell was successfully cast, false otherwise. If this returns false, the spell will not be
     * considered as having been cast, so no cooldown will be applied.
     */
    public boolean cast(LocationCastContext ctx) {
        return false;
    }

    /**
     * Called when the spell finishes casting, in case you want to make any special behavior happen when the spell
     * finishes casting, like applying a buff to the player or something. This is called regardless of whether the spell
     * is instant or not, so it will be called at the end of the cast() method for instant spells, and at the end of the
     * last tick of casting for non-instant spells.
     *
     * @param cxt The context of the spell cast, containing information about the world, caster (if any), location (if any),
     *            modifiers, etc.
     */
    public void endCast(CastContext cxt) {
    }

    /**
     * Called when the spell is on charge time, this is meant to be used for spells that have a charge-up time,
     * allowing you to implement the behavior of the spell during the charge-up time.
     *
     * @param ctx The context of the spell cast, containing information about the world, caster (if any), location (if any),
     *            modifiers, etc.
     */
    @Deprecated
    public void onCharge(CastContext ctx) {
    }

    /**
     * Whether this spell is instant or not. An instant spell is a spell that is cast in a single tick, (it could have
     * cooldown and/or charge-up time) and does not have a duration. By default, this returns true, as most spells are
     * instant, but you can override this to return false if your spell is meant to have a duration and be cast over
     * multiple ticks.
     *
     * @return true if this spell is instant, false otherwise.
     */
    public boolean isInstantCast() {
        return true;
    }

    /**
     * Whether this spell can be cast by an entity source (e.g. a player or a mob). By default, this returns false, as
     * some spells may be designed to only be cast by non-entity sources, but you can override this to return true if
     * your spell is meant to be cast by entities.
     *
     * @return true if this spell can be cast by an entity source, false otherwise.
     */
    public boolean canCastByEntity() {
        return false;
    }

    /**
     * Whether this spell can be cast by a non-entity source (e.g. a command block or a dispenser). By default, this returns
     * false, as most spells are meant to be cast by entities, but you can override this to return true if your spell is
     * designed to be cast by non-entity sources.
     *
     * @return true if this spell can be cast by a non-entity source, false otherwise.
     */
    public boolean canCastByLocation() {
        return false;
    }

    /**
     * Whether this spell requires a packet to be sent on client when it is cast. Returns true by default, but can be overridden
     * to return false <b>if</b> the spell's cast() method does not use any code that must be executed client-side (i.e.
     * particle spawning).
     * <p>
     * <i>If in doubt, leave this method as is; it is purely an optimization.</i>
     *
     * @return true if the spell code should be run on the server and all clients in the dimension, false if the spell
     * code should only be run on the server and the client of the player casting it.
     */
    public boolean requiresPacket() {
        return true;
    }

    // ===================================================
    // NAME AND FORMATTING
    // ==================================================

    /**
     * Gets the formatted description for this spell (e.g. Fireball with dark red color). By default, this is a translatable
     * component with the key "spell.[namespace].[path]", where [namespace] and [path] are the namespace and path of this
     * spell's location, respectively, and with the color of this spell's element.
     */
    public Component getDescriptionFormatted() {
        return Component.translatable(getOrCreateDescriptionId()).withStyle(this.getElement().getColor());
    }

    /**
     * Will return the description ID for the spell (e.g. "spell.ebwizardry.fireball"), in case it hasn't been set yet,
     * it will attempt to get it from the registry using the spell instance. This is used to avoid needing to set the
     * description ID manually for every spell, as it can be easily obtained from the registry and formatted.
     *
     * @return The description ID for this spell, used for formatting and translations. By default, this is
     * "spell.[namespace].[path]", where [namespace] and [path] are the namespace and path of this spell's location,
     * respectively.
     */
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null)
            this.descriptionId = Util.makeDescriptionId("spell", Services.REGISTRY_UTIL.getSpell(this));
        return this.descriptionId;
    }

    /**
     * Will return the description ID for the spell (e.g. "spell.ebwizardry.fireball")
     * if you want the location instead, use {@link #getLocation()}
     *
     * @return The description ID for this spell, used for formatting and translations. By default, this is
     * "spell.[namespace].[path]", where [namespace] and [path] are the namespace and path of this spell's location,
     * respectively.
     */
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    /**
     * Will return the location for the spell (e.g. "ebwizardry:fireball"), in case it hasn't been set yet, it will
     * attempt to get it from the registry using the spell instance. This is used to avoid needing to set the location
     * manually for every spell, as it can be easily obtained from the registry.
     *
     * @return The ResourceLocation where this spell is registered.
     */
    protected ResourceLocation getOrCreateLocation() {
        if (this.location == null) this.location = Services.REGISTRY_UTIL.getSpell(this);
        return this.location;
    }

    /**
     * Will return the location for the spell (e.g. "ebwizardry:fireball") if you want the description ID (formatted name)
     * instead, use {@link #getDescriptionId()}.
     *
     * @return The ResourceLocation where this spell is registered.
     */
    public ResourceLocation getLocation() {
        return this.getOrCreateLocation();
    }

    /**
     * Gets the description for this spell. By default, this is a translatable component with the key
     * "spell.[namespace].[path].desc", where [namespace] and [path] are the namespace and path of this spell's location,
     * respectively. You can override this method to provide a custom description for your spell in a different way.
     *
     * @return The description for this spell.
     */
    public Component getDesc() {
        return Component.translatable(getOrCreateDescriptionId() + ".desc");
    }

    /**
     * Gets the icon for this spell. By default, this is "textures/spells/[namespace]/[path].png", where [namespace] and
     * [path] are the namespace and path of this spell's location, respectively. You can override this method to provide a
     * custom icon for your spell in a different way.
     *
     * @return The ResourceLocation of the icon for this spell.
     */
    public ResourceLocation getIcon() {
        if (icon == null) {
            ResourceLocation location = getOrCreateLocation();
            this.icon = new ResourceLocation(location.getNamespace(), "textures/spells/" + location.getPath() + ".png");
        }
        return icon;
    }

    // ===================================================
    // PROPERTIES
    // ===================================================

    /**
     * This is just for internal use mostly, used to load the properties from the data files and vice versa. You should
     * use the {@code property(SpellProperty<T>)} method to get the value of a specific property.
     *
     * @return The SpellProperties object containing all properties for this spell.
     */
    public final SpellProperties getProperties() {
        return properties;
    }

    /**
     * This method is where you should set the default properties for your spell when creating a new spell class. This
     * method is called in the constructor of the Spell class, and the properties returned by this method are assigned
     * to the spell's properties field.
     *
     * @return A SpellProperties object with the default properties for your spell.
     */
    protected abstract @NotNull SpellProperties properties();

    /**
     * Helper method when you're creating spells that doesn't need a custom class (like {@code ArrowSpell} or
     * {@code ProjectileSpell} spells), allowing you to set the properties of the spell in a more concise way.
     *
     * @param properties The properties to set for this spell.
     * @return The spell instance, allowing this method to be chained onto the constructor.
     */
    public final Spell assignProperties(SpellProperties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Gets the value of the given property for this spell. This is a shortcut for {@code getProperties().get(property)}.
     * You should always use this method instead of directly calling getProperties().get(property) for better readability.
     *
     * @param property The property to get the value of.
     * @param <T>      The type of the property value.
     * @return The value of the given property for this spell.
     */
    public final <T> T property(SpellProperty<T> property) {
        return properties.get(property);
    }

    /**
     * Gets the charge-up time of this spell in ticks. By default, this is 0, meaning the spell is instant and has no
     * charge-up time.
     *
     * @return The charge-up time of this spell in ticks.
     */
    public int getChargeUp() {
        return properties.getChargeup();
    }

    /**
     * Gets the type of this spell. By default, this is {@link SpellType#UTILITY}.
     *
     * @return The type of this spell.
     */
    public SpellType getType() {
        return properties.getType();
    }

    /**
     * Gets the action of this spell. By default, this is {@link SpellAction#POINT}.
     *
     * @return The action of this spell.
     */
    public SpellAction getAction() {
        return properties.getAction();
    }

    /**
     * Gets the element of this spell. By default, this is {@link Elements#MAGIC}.
     *
     * @return The element of this spell.
     */
    public Element getElement() {
        return properties.getElement();
    }

    /**
     * Gets the tier of this spell. By default, this is {@link SpellTiers#NOVICE}.
     *
     * @return The tier of this spell.
     */
    public SpellTier getTier() {
        return properties.getTier();
    }

    /**
     * Gets the cost of this spell in spell points. By default, this is 0, meaning the spell is free to cast.
     * (You shouldn't leave this on 0 for most spells)
     *
     * @return The cost of this spell in spell points.
     */
    public int getCost() {
        return properties.getCost();
    }

    /**
     * Gets the cooldown of this spell in ticks. By default, this is 0, meaning the spell has no cooldown.
     *
     * @return The cooldown of this spell in ticks.
     */
    public int getCooldown() {
        return properties.getCooldown();
    }

    /**
     * Checks if this spell is enabled in the given context. By default, all contexts are enabled, it's the user's
     * responsibility to disable the contexts in the mod config.
     *
     * @param context The context to check.
     * @return true if this spell is enabled in the given context, false otherwise.
     */
    public boolean isEnabled(SpellContext context) {
        return properties.isEnabledInContext(context);
    }

    @Override
    public String toString() {
        return getLocation().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Spell other = (Spell) obj;
        return getLocation().equals(other.getLocation());
    }

    // ===================================================
    // SOUND SYSTEM
    // ===================================================

    /**
     * Sets the sound parameters for this spell.
     *
     * @param volume         The volume of the sound played by this spell, relative to 1.
     * @param pitch          The pitch of the sound played by this spell, relative to 1.
     * @param pitchVariation The random variation in the pitch of the sound played by this spell. The pitch at which
     *                       the sound is played will be randomly chosen from the range: {@code pitch +/- pitchVariation}.
     * @return The spell instance, allowing this method to be chained onto the constructor.
     */
    public Spell soundValues(float volume, float pitch, float pitchVariation) {
        this.volume = volume;
        this.pitch = pitch;
        this.pitchVariation = pitchVariation;
        return this;
    }

    /**
     * Gets the volume for this spell.
     *
     * @return The volume of the sound played by this spell, relative to 1.
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Gets the pitch for this spell. The pitch at which the sound is played will be randomly chosen from the range:
     * {@code getPitch() +/- getPitchVariation()}.
     *
     * @return The base pitch of the sound played by this spell.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Gets the pitch variation for this spell. The pitch at which the sound is played will be randomly chosen from the
     * range: {@code getPitch() +/- getPitchVariation()}.
     *
     * @return The random variation in the pitch of the sound played by this spell.
     */
    public float getPitchVariation() {
        return pitchVariation;
    }

    /**
     * Plays this spell's sound at the given entity in the given world. This calls {@link Spell#playSound(Level, double,
     * double, double, int, int)}, passing in the given entity's position as the xyz coordinates. Also checks if the given
     * entity is silent, and if so, does not play the sound.
     * <p>
     * You should override this is you're trying to implement a custom sound loop, check {@link FrostRay FrostRaySpell}
     * as an example.
     *
     * @param world     The world to play the sound in.
     * @param entity    The entity to play the sound at, provided it is not silent.
     * @param castTicks The number of ticks this spell has already been cast for, passed in from the {@code cast(...)}
     *                  methods. <i>Not used in the base method, but included for use by subclasses overriding this method.</i>
     * @param duration  The number of ticks this spell will be cast for, passed in from the {@code cast(...)}
     *                  methods. <i>Not used in the base method, but included for use by subclasses overriding this method.</i>
     */
    protected void playSound(Level world, LivingEntity entity, int castTicks, int duration) {
        if (!entity.isSilent())
            playSound(world, entity.getX(), entity.getY(), entity.getZ(), castTicks, duration);
    }

    /**
     * Plays this spell's sound at the given position in the given world. This is a vector-based wrapper for
     * {@link Spell#playSound(Level, double, double, double, int, int)}.
     * <p>
     * <b>This is not called automatically by the Spell class</b>; subclasses should call it at the appropriate point(s)
     * in the cast methods. If you're using any standard subclass of Spell (e.g. {@link RaySpell RaySpell}
     * {@link ArrowSpell ArrowSpell}) you won't need to handle the sound system by yourself, as these classes will call
     * the playSound in the right moment for you.
     *
     * @param world     The world to play the sound in.
     * @param pos       A vector representing the position to play the sound at.
     * @param castTicks The number of ticks this spell has already been cast for, passed in from the {@code cast(...)}
     *                  methods. <i>Not used in the base method, but included for use by subclasses overriding this method.</i>
     * @param duration  The number of ticks this spell will be cast for, passed in from the {@code cast(...)}
     *                  methods. <i>Not used in the base method, but included for use by subclasses overriding this method.</i>
     */
    protected void playSound(Level world, Vec3 pos, int castTicks, int duration) {
        playSound(world, pos.x, pos.y, pos.z, castTicks, duration);
    }

    /**
     * Plays this spell's sounds at the given position in the given world. <b>This is not called automatically by the
     * Spell class</b>; subclasses should call it at the appropriate point(s) in the cast methods. You can also override
     * this method if you want to implement a sound loop (normally for continuous spells), check
     * {@link FrostRay FrostRaySpell} as an example.
     * <p>
     * In case you're using any standard subclass of Spell (e.g. {@link RaySpell RaySpell} {@link ArrowSpell ArrowSpell})
     * you won't need to handle the sound system by yourself, as these classes will call the playSound in the right moment
     * for you.
     *
     * @param world      The world to play the sound in.
     * @param x          The x position to play the sound at.
     * @param y          The y position to play the sound at.
     * @param z          The z position to play the sound at.
     * @param ticksInUse The number of ticks this spell has already been cast for, passed in from the {@code cast(...)} methods.
     * @param duration   The number of ticks this spell will be cast for, passed in from the {@code cast(...)} methods.
     *                   <i>Not used in the base method, but included for use by subclasses overriding this method.</i>
     */
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
        SoundEvent sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(getLocation().getNamespace(), "spell." + getLocation().getPath()));
        world.playSound(null, x, y, z, sound, SoundSource.PLAYERS, getVolume(), getPitch() + getPitchVariation() * (world.random.nextFloat() - 0.5f));
    }

    /**
     * Start the standard continuous spell sound loop on the given entity at the first cast tick. This is likely happening
     * with a player or some living-entity-based casting logic, so the sound loop will be played at the given entity's
     * position and will follow the entity as it moves. The sound loop will only be started if the given entity is not silent,
     * and if this is the first tick of casting (i.e. ticksInUse is 0), so that the sound loop starts on the first tick
     * of casting and does not start at all if the entity is silent.
     *
     * @param world      The world to play the sound in.
     * @param entity     The entity to play the sound at, provided it is not silent
     * @param ticksInUse The number of ticks this spell has already been cast for, passed in from the {@code cast(...)}
     *                   methods. This method will only play the sound loop if this is 0, so that the sound loop starts
     *                   on the first tick of casting.
     */
    protected final void playSoundLoop(Level world, LivingEntity entity, int ticksInUse) {
        if (ticksInUse == 0 && world.isClientSide)
            ClientSpellSoundManager.playSpellSoundLoop(entity, this, getLoopSounds(), volume, pitch + pitchVariation * (world.random.nextFloat() - 0.5f));
    }

    /**
     * Start the standard continuous spell sound loop at the given position on the first cast tick. This is likely
     * happening with a dispenser or some static casting logic (like casting a spell from a command) so the sound loop will
     * be played at the given position.
     *
     * @param world      The world to play the sound in.
     * @param x          The x position to play the sound at.
     * @param y          The y position to play the sound at.
     * @param z          The z position to play the sound at.
     * @param ticksInUse The number of ticks this spell has already been cast for, passed in from the {@code cast(...)}
     *                   methods. This method will only play the sound loop if this is 0, so that the sound loop starts
     *                   on the first tick of casting.
     * @param duration   The number of ticks this spell will be cast for, passed in from the {@code cast(...)} methods.
     *                   This is used to determine how long the sound loop should last; if this is -1, the sound loop
     *                   will last until the spell ends, otherwise it will last for the given number of ticks.
     */
    protected final void playSoundLoop(Level world, double x, double y, double z, int ticksInUse, int duration) {
        SoundEvent[] loopSounds = getLoopSounds();
        if (ticksInUse == 0 && world.isClientSide && loopSounds.length == 3) {
            playSpellSoundLoop(world, x, y, z, this, loopSounds, volume, pitch + pitchVariation * (world.random.nextFloat() - 0.5f), duration);
        }
    }

    /**
     * Helper method that you could change if you want to add/change the names of the 3 sound events that are used for
     * the standard continuous spell sound loop. Keep in mind that if you want to add more than 3 sound events, you will
     * need to make your own implementation to handle the extra sound events.
     * <p>
     * By default, this method assumes that the sound events are named "spell.[namespace].[path].start",
     * "spell.[namespace].[path].loop", and "spell.[namespace].[path].end", where [namespace] and [path] are the namespace
     * and path of this spell's location, respectively.
     *
     * @return By default, an array of 3 sound events, in the order of start, loop, and end sounds.
     */
    protected SoundEvent[] getLoopSounds() {
        List<String> names = List.of("start", "loop", "end");
        return names.stream().map(name -> SoundEvent.createVariableRangeEvent(new ResourceLocation(this.getLocation().getNamespace(), "spell." + this.getLocation().getPath() + "." + name))).toArray(SoundEvent[]::new);
    }
}
