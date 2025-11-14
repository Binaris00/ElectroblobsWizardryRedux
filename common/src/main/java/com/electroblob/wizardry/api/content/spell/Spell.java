package com.electroblob.wizardry.api.content.spell;

import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.LocationCastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.core.ClientSpellSoundManager;
import com.electroblob.wizardry.core.platform.Services;
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

import static com.electroblob.wizardry.core.ClientSpellSoundManager.playSpellSoundLoop;

/**
 * The Spell class serves as a blueprint for different types of spells.
 * Subclasses inherit from it to ensure
 * consistency.
 * By defining common behavior and methods in the Spell class, we establish
 * a standardized interface for interacting with spells throughout the codebase.
 * This makes the code more organized, easier to understand, and less prone to errors.
 */
public abstract class Spell {
    protected float volume = 1;
    protected float pitch = 1;
    protected float pitchVariation = 0;
    /**
     * Description ID is how a spell is formatted, with the same format as items or blocks.
     * e.g. "spell.ebwizardry.fireball"
     */
    private String descriptionId;
    /**
     * Location is where the spell is registered, e.g. "ebwizardry:fireball"
     */
    private ResourceLocation location;
    private ResourceLocation icon;
    private SpellProperties properties = SpellProperties.empty();
    private boolean ended;

    public Spell() {
        this.properties = properties();
    }

    public abstract boolean cast(PlayerCastContext ctx);

    public boolean cast(EntityCastContext ctx) {
        return false;
    }

    public boolean cast(LocationCastContext ctx) {
        return false;
    }

    public void endCast(CastContext cxt) {
        this.ended = true;
    }

    public void onCharge(CastContext ctx) {
    }

    public final boolean hasEnded() {
        return ended;
    }

    public boolean isInstantCast() {
        return true;
    }

    public boolean canCastByEntity() {
        return false;
    }

    public boolean canCastByLocation() {
        return false;
    }

    public final Boolean isEmpty() {
        return this instanceof NoneSpell;
    }

    // ===================================================
    // NAME AND FORMATTING
    // ==================================================

    /**
     * Will return the description for the spell (e.g. "Fireball")
     */
    public Component getDescriptionFormatted() {
        return Component.translatable(getOrCreateDescriptionId()).withStyle(this.getElement().getColor());
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null)
            this.descriptionId = Util.makeDescriptionId("spell", Services.REGISTRY_UTIL.getSpell(this));
        return this.descriptionId;
    }

    /**
     * Will return the description ID for the spell (e.g. "spell.ebwizardry.fireball")
     * if you want the location instead, use {@link #getLocation()}
     */
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    protected ResourceLocation getOrCreateLocation() {
        if (this.location == null) this.location = Services.REGISTRY_UTIL.getSpell(this);
        return this.location;
    }

    /**
     * Will return the location for the spell (e.g. "ebwizardry:fireball")
     */
    public ResourceLocation getLocation() {
        return this.getOrCreateLocation();
    }

    public Component getDesc() {
        return Component.translatable(getOrCreateDescriptionId() + ".desc");
    }

    public ResourceLocation getIcon() {
        if (icon == null) {
            ResourceLocation location = getOrCreateLocation();
            this.icon = new ResourceLocation(location.getNamespace(), "textures/spells/" + location.getPath() + ".png");
        }
        return icon;
    }

    /**
     * Will return true if the spell is registered at the given location
     */
    public final boolean is(ResourceLocation location) {
        return location.equals(getLocation());
    }

    /**
     * Will return true if the spell is registered at the given location
     */
    public final boolean is(String location) {
        return location.equals(getLocation().toString());
    }

    // ===================================================
    // PROPERTIES
    // ===================================================
    public final SpellProperties getProperties() {
        return properties;
    }

    public void setProperties(SpellProperties properties) {
        this.properties = properties;
    }

    protected abstract @NotNull SpellProperties properties();

    public final Spell assignProperties(SpellProperties properties) {
        this.properties = properties;
        return this;
    }

    public final <T> T property(SpellProperty<T> property) {
        return properties.get(property);
    }

    public final boolean is(Spell spell) {
        return spell.getDescriptionId().equals(this.descriptionId);
    }

    public int getCharge() {
        return properties.getCharge();
    }

    public SpellType getType() {
        return properties.getType();
    }

    public SpellAction getAction() {
        return properties.getAction();
    }

    public Element getElement() {
        return properties.getElement();
    }

    public SpellTier getTier() {
        return properties.getTier();
    }

    public int getCost() {
        return properties.getCost();
    }

    public int getCooldown() {
        return properties.getCooldown();
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

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public float getPitchVariation() {
        return pitchVariation;
    }

    /**
     * Plays this spell's sound at the given entity in the given world. This calls
     * {@link Spell#playSound(Level, double, double, double, int, int)}, passing in the
     * given entity's position as the xyz coordinates. Also checks if the given entity is silent, and if so,
     * does not play the sound.
     * <br><br>
     * You should override this is you're trying to implement a custom sound loop, check
     * {@link com.electroblob.wizardry.content.spell.ice.FrostRay FrostRaySpell} as an example.
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
     * Plays this spell's sounds at the given position in the given world. <b>This is not called automatically</b>;
     * subclasses should call it at the appropriate point(s) in the cast methods. Extend this method entirely if you
     * want to add extra sounds or change the sound behaviour.
     *
     * @param world The world to play the sound in.
     * @param x     The x position to play the sound at.
     * @param y     The y position to play the sound at.
     * @param z     The z position to play the sound at.
     */
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
        SoundEvent sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(getLocation().getNamespace(), "spell." + getLocation().getPath()));
        world.playSound(null, x, y, z, sound, SoundSource.PLAYERS, getVolume(), getPitch() + getPitchVariation() * (world.random.nextFloat() - 0.5f));
    }

    /**
     * Helper method which plays a standard continuous spell sound loop on the first casting tick, which moves
     * with the given entity.
     */
    protected final void playSoundLoop(Level world, LivingEntity entity, int ticksInUse) {
        if (ticksInUse == 0 && world.isClientSide)
            ClientSpellSoundManager.playSpellSoundLoop(entity, this, getLoopSounds(), volume, pitch + pitchVariation * (world.random.nextFloat() - 0.5f));
    }

    /**
     * Helper method which plays a standard continuous spell sound loop on the first casting tick, at the given
     * coordinates. If the given duration is -1, the coordinates must be those of a dispenser.
     */
    protected final void playSoundLoop(Level world, double x, double y, double z, int ticksInUse, int duration) {
        if (ticksInUse == 0 && world.isClientSide) {
            playSpellSoundLoop(world, x, y, z, this, getLoopSounds(), volume,
                    pitch + pitchVariation * (world.random.nextFloat() - 0.5f), duration);
        }
    }

    /**
     * Helper method that you could change if you want to add/change the behavior of the sound loops.
     */
    protected SoundEvent[] getLoopSounds() {
        List<String> names = List.of("start", "loop", "end");
        return names.stream().map(name -> SoundEvent.createVariableRangeEvent(new ResourceLocation(this.getLocation().getNamespace(), "spell." + this.getLocation().getPath() + "." + name))).toArray(SoundEvent[]::new);
    }
}
