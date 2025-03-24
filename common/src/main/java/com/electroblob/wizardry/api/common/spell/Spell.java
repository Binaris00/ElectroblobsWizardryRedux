package com.electroblob.wizardry.api.common.spell;


import com.electroblob.wizardry.setup.SpellSoundManager;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.common.core.SpellEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static com.electroblob.wizardry.api.common.spell.CastingPhase.*;

/**
 * The Spell class serves as a blueprint for different types of spells.
 * Subclasses inherit from it to ensure
 * consistency.
 * By defining common behavior and methods in the Spell class, we establish
 * a standardized interface for interacting with spells throughout the codebase.
 * This makes the code more organized, easier to understand, and less prone to errors.
*/
public abstract class Spell implements Cloneable {
    private ResourceLocation location;
    private boolean propertiesFrozen;
    private SpellProperties properties = SpellProperties.empty();
    private boolean ended;

    private CastingPhase phase = NONE; // This should never be null
    private int prepareCastTime;
    private int performCastTime;
    private int concludeCastTime;

    protected float volume = 1;
    protected float pitch = 1;
    protected float pitchVariation = 0;

    public Spell() {
        if(properties() != null) {
            this.properties = properties();
            propertiesFrozen = true;
        }
    }

    final Spell assignLocation(ResourceLocation location) {
        if(this.location == null && SpellRegistry.isInAssignPeriod()) this.location = location;
        return this;
    }

    public final Spell assignProperties(SpellProperties properties) {
        if(!propertiesFrozen) {
            this.properties = properties;
            propertiesFrozen = true;
        }
        return this;
    }

    public final Boolean isEmpty(){
        return this instanceof NoneSpell;
    }

    public final void cast(@NotNull Caster caster) {
        EBLogger.info("cast() called. Phase: " + phase + ", Ended: " + ended);
        execute_cast(caster);
    }

    private void execute_cast(@NotNull Caster caster) {
        EBLogger.info("execute_cast() called. Phase: " + phase + ", Ended: " + ended);
        if(!phase.isCasting()) {
            phase = PREPARE;
        }
        updateCast(caster);
        if(!this.ended) {
            EBLogger.info("Adding spell to SpellEngine.");
            SpellEngine.addSpellToLevel(this, caster);
        }
    }

    public final void updateCast(Caster caster){
        EBLogger.info("updateCast() called. Phase: " + phase + ", prepareCastTime: " + prepareCastTime + ", performCastTime: " + performCastTime + ", concludeCastTime: " + concludeCastTime);
        switch(this.phase){
            case PREPARE -> updatePrepare(caster);
            case PERFORM -> updatePerform(caster);
            case CONCLUDE -> updateConclude(caster);
        }
    }

    private void updatePrepare(Caster caster) {
        EBLogger.info("updatePrepare() called. prepareCastTime: " + prepareCastTime);
        if(prepareCastTime == 0) onPrepareStart(caster);
        prepare(caster);
        prepareCastTime++;

        if(readyToPerform(caster)) {
            onPrepareEnd(caster);
            phase = PERFORM;
            EBLogger.info("Transitioning to PERFORM phase.");
            if(updateNextImmediately()) perform(caster);
        }
    }

    private void updatePerform(Caster caster){
        EBLogger.info("updatePerform() called. performCastTime: " + performCastTime);
        if(performCastTime == 0) onPerformStart(caster);
        perform(caster);
        performCastTime++;

        if(readyToConclude(caster)) {
            onPerformEnd(caster);
            phase = CONCLUDE;
            EBLogger.info("Transitioning to CONCLUDE phase.");
            if(updateNextImmediately()) conclude(caster);
        }
    }

    private void updateConclude(Caster caster){
        EBLogger.info("updateConclude() called. concludeCastTime: " + concludeCastTime);
        if(concludeCastTime == 0) onConcludeStart(caster);
        conclude(caster);
        concludeCastTime++;

        if(readyToEnd(caster)) {
            onConcludeEnd(caster);
            endCast(caster);
            EBLogger.info("Spell casting ended.");
        }
    }

    private void endCast(Caster caster){
        EBLogger.info("endCast() called. Marking spell as ended.");
        this.phase = NONE;
        this.ended = true;
    }


    protected void prepare(Caster caster) {}
    protected abstract void perform(Caster caster);
    protected void conclude(Caster caster) {}


    protected void onPrepareStart(Caster caster) {}
    protected void onPerformStart(Caster caster) {}
    protected void onConcludeStart(Caster caster) {}
    protected void onPrepareEnd(Caster caster) {}
    protected void onPerformEnd(Caster caster) {}
    protected void onConcludeEnd(Caster caster) {}


    protected boolean readyToPerform(Caster caster) {
        return prepareCastTime >= prepareDuration() || isInstantCast();
    }

    protected boolean readyToConclude(Caster caster) {
        return performCastTime >= performDuration() || isInstantCast();
    }

    protected boolean readyToEnd(Caster caster) {
        return concludeCastTime >= concludeDuration() || isInstantCast();
    }

    /**
     * If the next phase is ready should it update on the same tick or wait until next tick?
     */
    protected boolean updateNextImmediately() {
        return isInstantCast();
    }

    public boolean isInstantCast() {
        return prepareDuration() == 0 && performDuration() == 0 && concludeDuration() == 0;
    }

    public int prepareDuration() {
        return 0;
    }
    public int performDuration() {
        return 0;
    }
    public int concludeDuration() {
        return 0;
    }

    public final boolean hasEnded() {
        return ended;
    }

    protected abstract SpellProperties properties();

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
        if (!entity.isSilent()) {
            SpellSoundManager.playSound(world, this, entity.getX(), entity.getY(), entity.getZ(), ticksInUse, duration);
        }
    }

    protected void playSound(Level world, Vec3 pos, int ticksInUse, int duration) {
        SpellSoundManager.playSound(world, this, pos.x, pos.y, pos.z, ticksInUse, duration);
    }

    @Override
    public Spell clone() {
        try {
            return (Spell) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
