package com.electroblob.wizardry.api.common.spell;


import com.electroblob.wizardry.common.core.SpellEngine;
import net.minecraft.resources.ResourceLocation;
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

    // Initiate Cast
    // This should only be invoked whenever caster is real
    public final void cast(@NotNull Caster caster) {
        execute_cast(caster);
    }

    private void execute_cast(@NotNull Caster caster) {
        if(!phase.isCasting()) phase = PREPARE;
        updateCast(caster); // Initial cast the runs when invoked (this should be called on game thread)
        if(!this.ended) SpellEngine.addSpellToLevel(this, caster); // Spells that are instant never get loaded on spell engine
    }

    public final void updateCast(Caster caster){
        switch(this.phase){
            case PREPARE -> updatePrepare(caster);
            case PERFORM -> updatePerform(caster);
            case CONCLUDE -> updateConclude(caster);
        }
    }

    private void updatePrepare(Caster caster) {
        if(prepareCastTime == 0) onPrepareStart(caster);
        prepare(caster);
        prepareCastTime++;

        if(readyToPerform(caster)) {
            onPrepareEnd(caster);
            phase = PERFORM;
            if(updateNextImmediately()) perform(caster);
        }
    }


    private void updatePerform(Caster caster){
        if(performCastTime == 0) onPerformStart(caster);
        perform(caster);
        performCastTime++;

        if(readyToConclude(caster)) {
            onPerformEnd(caster);
            phase = CONCLUDE;
            if(updateNextImmediately()) conclude(caster);
        }
    }

    private void updateConclude(Caster caster){
        if(concludeCastTime == 0) onConcludeStart(caster);
        conclude(caster);
        concludeCastTime++;

        if(readyToEnd(caster)) {
            onConcludeEnd(caster);
            endCast(caster);
        }
    }

    private void endCast(Caster caster){
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

    @Override
    public Spell clone() {
        try {
            return (Spell) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
