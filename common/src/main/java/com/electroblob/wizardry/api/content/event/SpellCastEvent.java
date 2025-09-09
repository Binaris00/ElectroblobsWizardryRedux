package com.electroblob.wizardry.api.content.event;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.core.event.IWizardryEvent;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class SpellCastEvent implements IWizardryEvent {
    private final Spell spell;
    private final SpellModifiers modifiers;
    private final Source source;
    private final @Nullable LivingEntity caster;
    private final Level world;
    private final double x, y, z;
    private final Direction direction;


    public SpellCastEvent(Source source, Spell spell, LivingEntity caster, SpellModifiers modifiers){
        super();
        this.spell = spell;
        this.modifiers = modifiers;
        this.source = source;
        this.caster = caster;
        this.world = caster.level();
        this.x = Double.NaN; // Better to use NaN than some arbitrary number, because NaN will throw an exception when
        this.y = Double.NaN; // someone tries to operate on it whereas 0, for example, will likely just cause strange
        this.z = Double.NaN; // behaviour - the cause of which may not be immediately obvious.
        this.direction = null;
    }

    public SpellCastEvent(Source source, Spell spell, Level world, double x, double y, double z, Direction direction, SpellModifiers modifiers){
        super();
        this.spell = spell;
        this.modifiers = modifiers;
        this.source = source;
        this.caster = null;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
    }

    public Spell getSpell(){
        return spell;
    }

    public SpellModifiers getModifiers(){
        return modifiers;
    }

    public Source getSource(){
        return source;
    }

    @Nullable
    public LivingEntity getCaster(){
        return caster;
    }

    public Level getLevel(){
        return world;
    }

    /**This is could be NaN if there's no pos*/
    public double getX(){
        return x;
    }

    /**This is could be NaN if there's no pos*/
    public double getY(){
        return y;
    }

    /**This is could be NaN if there's no pos*/
    public double getZ(){
        return z;
    }

    @Nullable
    public Direction getDirection(){
        return direction;
    }

    private boolean isCanceled;

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.isCanceled = cancel;
    }

    public enum Source {
        WAND,
        SCROLL,
        NPC,
        DISPENSER,
        OTHER,
        COMMAND
    }

    public static class Pre extends SpellCastEvent {
        public Pre(Source source, Spell spell, LivingEntity caster, SpellModifiers modifiers){
            super(source, spell, caster, modifiers);
        }

        public Pre(Source source, Spell spell, Level world, double x, double y, double z, Direction direction, SpellModifiers modifiers){
            super(source, spell, world, x, y, z, direction, modifiers);
        }

        @Override
        public boolean canBeCanceled() {
            return true;
        }
    }

    public static class Post extends SpellCastEvent {

        public Post(Source source, Spell spell, LivingEntity caster, SpellModifiers modifiers){
            super(source, spell, caster, modifiers);
        }

        public Post(Source source, Spell spell, Level world, double x, double y, double z, Direction direction, SpellModifiers modifiers){
            super(source, spell, world, x, y, z, direction, modifiers);
        }

        @Override
        public boolean canBeCanceled() {
            return false;
        }
    }

    public static class Tick extends SpellCastEvent {
        private final int ticksCasting;

        public Tick(Source source, Spell spell, LivingEntity caster, SpellModifiers modifiers, int ticks){
            super(source, spell, caster, modifiers);
            this.ticksCasting = ticks;
        }

        public Tick(Source source, Spell spell, Level world, double x, double y, double z, Direction direction, SpellModifiers modifiers, int ticks){
            super(source, spell, world, x, y, z, direction, modifiers);
            this.ticksCasting = ticks;
        }

        public int getTicksCasting(){
            return ticksCasting;
        }

        @Override
        public boolean canBeCanceled() {
            return true;
        }
    }

    public static class Finish extends SpellCastEvent {
        private final int ticksCasting;

        public Finish(Source source, Spell spell, LivingEntity caster, SpellModifiers modifiers, int ticks){
            super(source, spell, caster, modifiers);
            this.ticksCasting = ticks;
        }

        public Finish(Source source, Spell spell, Level world, double x, double y, double z, Direction direction, SpellModifiers modifiers, int ticks){
            super(source, spell, world, x, y, z, direction, modifiers);
            this.ticksCasting = ticks;
        }

        public int getTicksCasting(){
            return ticksCasting;
        }

        @Override
        public boolean canBeCanceled() {
            return false;
        }
    }

}
