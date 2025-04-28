package com.electroblob.wizardry.api.content.spell;

import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.LocationCastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.core.SpellSoundManager;
import com.electroblob.wizardry.core.registry.SpellRegistry;
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
    private ResourceLocation location;
    private SpellProperties properties = SpellProperties.empty();
    private boolean ended;

    protected float volume = 1;
    protected float pitch = 1;
    protected float pitchVariation = 0;

    public Spell() {
        if(properties() != null) {
            this.properties = properties();
        }
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
    // PROPERTIES
    // ===================================================

    protected abstract @NotNull SpellProperties properties();

    public final Spell assignLocation(ResourceLocation location) {
        if(this.location == null && SpellRegistry.isInAssignPeriod()) this.location = location;
        return this;
    }

    public final Spell assignProperties(SpellProperties properties) {
        this.properties = properties;
        return this;
    }

    public final ResourceLocation getLocation() {
        return location;
    }

    public final <T> T property(SpellProperty<T> property) {
        return properties.get(property);
    }

    public final boolean is(Spell spell) {
        return spell.getLocation().equals(this.location);
    }

    public final boolean is(ResourceLocation location) {
        return location.equals(this.location);
    }

    public final boolean is(String location) {
        return location.equals(this.location.toString());
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

    public Tier getTier(){
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
