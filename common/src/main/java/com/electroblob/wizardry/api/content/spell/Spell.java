package com.electroblob.wizardry.api.content.spell;

import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.LocationCastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.core.SpellSoundManager;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The Spell class serves as a blueprint for different types of spells.
 * Subclasses inherit from it to ensure
 * consistency.
 * By defining common behavior and methods in the Spell class, we establish
 * a standardized interface for interacting with spells throughout the codebase.
 * This makes the code more organized, easier to understand, and less prone to errors.
*/
public abstract class Spell {
    /**Description ID is how a spell is formatted, with the same format as items or blocks.
     * e.g. "spell.ebwizardry.fireball" */
    private String descriptionId;
    /**Location is where the spell is registered, e.g. "ebwizardry:fireball" */
    private ResourceLocation location;
    private ResourceLocation icon;

    private SpellProperties properties = SpellProperties.empty();
    private boolean ended;

    protected float volume = 1;
    protected float pitch = 1;
    protected float pitchVariation = 0;

    public Spell() {
        this.properties = properties();
    }

    public abstract boolean cast(PlayerCastContext ctx);

    public boolean cast(EntityCastContext ctx){
        return false;
    }

    public boolean cast(LocationCastContext ctx){
        return false;
    }

    public void endCast(CastContext cxt){
        this.ended = true;
    }

    public void onCharge(CastContext ctx){
    }

    public final boolean hasEnded() {
        return ended;
    }

    public boolean isInstantCast() {
        return true;
    }

    public boolean canCastByEntity(){
        return false;
    }

    public boolean canCastByLocation(){
        return false;
    }

    public final Boolean isEmpty(){
        return this instanceof NoneSpell;
    }

    // ===================================================
    // NAME AND FORMATTING
    // ==================================================
    /** Will return the description for the spell (e.g. "Fireball") */
    public Component getDescriptionFormatted() {
        return Component.translatable(getOrCreateDescriptionId());
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) this.descriptionId = Util.makeDescriptionId("spell", Services.REGISTRY_UTIL.getSpell(this));
        return this.descriptionId;
    }

    /** Will return the description ID for the spell (e.g. "spell.ebwizardry.fireball")
     * if you want the location instead, use {@link #getLocation()} */
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    protected ResourceLocation getOrCreateLocation() {
        if (this.location == null) this.location = Services.REGISTRY_UTIL.getSpell(this);
        return this.location;
    }

    /** Will return the location for the spell (e.g. "ebwizardry:fireball") */
    public ResourceLocation getLocation() {
        return this.getOrCreateLocation();
    }

    public Component getDesc(){
        return Component.translatable(getOrCreateDescriptionId() + ".desc");
    }

    public ResourceLocation getIcon(){
        if(icon == null) {
            ResourceLocation location = getOrCreateLocation();
            this.icon = new ResourceLocation(location.getNamespace(), "textures/spells/" + location.getPath() + ".png");
        }
        return icon;
    }

    /** Will return true if the spell is registered at the given location */
    public final boolean is(ResourceLocation location) {
        return location.equals(getLocation());
    }

    /** Will return true if the spell is registered at the given location */
    public final boolean is(String location) {
        return location.equals(getLocation().toString());
    }

    // ===================================================
    // PROPERTIES
    // ===================================================

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

    public int getCharge(){
        return properties.getCharge();
    }

    public SpellType getType(){
        return properties.getType();
    }

    public SpellAction getAction(){
        return properties.getAction();
    }

    public Element getElement(){
        return properties.getElement();
    }

    public SpellTier getTier(){
        return properties.getTier();
    }

    public int getCost(){
        return properties.getCost();
    }

    public int getCooldown(){
        return properties.getCooldown();
    }

    // ===================================================
    // SOUND SYSTEM
    // ===================================================
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

    protected void playSound(Level world, LivingEntity entity, int ticksInUse, int duration) {
        if (!entity.isSilent()) SpellSoundManager.playSound(world, this, entity.getX(), entity.getY(), entity.getZ(), ticksInUse, duration);
    }

    protected void playSound(Level world, Vec3 pos, int ticksInUse, int duration) {
        SpellSoundManager.playSound(world, this, pos.x, pos.y, pos.z, ticksInUse, duration);
    }

    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
        SpellSoundManager.playSound(world, this, x, y, z, ticksInUse, duration);
    }

    protected final void playSoundLoop(Level world, LivingEntity entity, int ticksInUse) {
        if (ticksInUse == 0 && world.isClientSide)
            SpellSoundManager.playSpellSoundLoop(entity, this, getLoopSounds(), volume, pitch + pitchVariation * (world.random.nextFloat() - 0.5f));
    }

    protected final void playSoundLoop(Level world, double x, double y, double z, int ticksInUse, int duration) {
        if (ticksInUse == 0 && world.isClientSide) {
            SpellSoundManager.playSpellSoundLoop(world, x, y, z, this, getLoopSounds(), volume, pitch + pitchVariation * (world.random.nextFloat() - 0.5f), duration);
        }
    }

    protected SoundEvent[] getLoopSounds() {
        List<String> names = List.of("start", "loop", "end");
        return names.stream().map(name -> SoundEvent.createVariableRangeEvent(new ResourceLocation(
                this.getLocation().getNamespace(), "spell." + this.getLocation().getPath() + "." + name))).toArray(SoundEvent[]::new);
    }

    // ============
}
