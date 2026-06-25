package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.*;
import com.binaris.wizardry.core.event.IWizardryEvent;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for all events fired during the spell casting lifecycle. Each sub-event corresponds to a distinct phase of casting and
 * carries a {@link CastContext} that fully describes the origin, caster, modifiers, and world state of the cast.
 *
 * @see CastContext        Base context class
 * @see Source             Interface for identifying the cast origin type
 * @see Sources            Built-in {@link Source} implementations
 */
public abstract class SpellCastEvent implements IWizardryEvent {
    private final Spell spell;
    private final CastContext context;
    private final Source source;
    private boolean isCanceled;

    /**
     * Creates a new {@code SpellCastEvent} backed by a {@link CastContext}.
     *
     * @param source  the origin type of this cast (see {@link Sources} for built-in values)
     * @param spell   the spell being cast
     * @param context the full casting context describing who/what/where is casting
     */
    public SpellCastEvent(Source source, Spell spell, CastContext context) {
        this.spell = spell;
        this.context = context;
        this.source = source;
    }

    /**
     * @deprecated Use {@link #SpellCastEvent(Source, Spell, CastContext)} with an {@link EntityCastContext} instead.
     */
    @Deprecated(forRemoval = true)
    public SpellCastEvent(Source source, Spell spell, LivingEntity caster, SpellModifiers modifiers) {
        this(source, spell, new EntityCastContext(caster.level(), caster, InteractionHand.MAIN_HAND, 0, null, modifiers));
    }

    /**
     * @deprecated Use {@link #SpellCastEvent(Source, Spell, CastContext)} with a {@link LocationCastContext} instead.
     */
    @Deprecated(forRemoval = true)
    public SpellCastEvent(Source source, Spell spell, Level world, double x, double y, double z, Direction direction, SpellModifiers modifiers) {
        this(source, spell, new LocationCastContext(world, x, y, z, direction, 0, 0, modifiers));
    }

    /**
     * Returns the spell associated with this event.
     *
     * @return the {@link Spell} being cast
     */
    public Spell getSpell() {
        return spell;
    }

    /**
     * Returns the full {@link CastContext} for this event. Use this to access context-specific data beyond what the convenience
     * delegators expose.
     *
     * @return the {@link CastContext} describing this cast
     */
    public CastContext getContext() {
        return context;
    }

    /**
     * Returns the {@link Source} that triggered this cast.
     *
     * @return the cast {@link Source}
     */
    public Source getSource() {
        return source;
    }

    /**
     * Convenience delegator. Returns the living entity responsible for this cast,
     * or {@code null} if the cast originated from a fixed location ({@link LocationCastContext}).
     *
     * @return the caster {@link LivingEntity}, or {@code null}
     * @see CastContext#caster()
     */
    @Nullable
    public LivingEntity getCaster() {
        return context.caster();
    }

    /**
     * Convenience delegator. Returns the world in which the spell is being cast.
     *
     * @return the {@link Level}
     * @see CastContext#world()
     */
    public Level getLevel() {
        return context.world();
    }

    /**
     * Convenience delegator. Returns the {@link SpellModifiers} applied to this cast.
     *
     * @return the active {@link SpellModifiers}
     * @see CastContext#modifiers()
     */
    public SpellModifiers getModifiers() {
        return context.modifiers();
    }

    /**
     * Convenience delegator. Returns the X coordinate of the cast origin.
     *
     * @return X origin coordinate, or {@link Double#NaN} if not a location cast
     * @see LocationCastContext#x()
     */
    public double getX() {
        return context instanceof LocationCastContext loc ? loc.x() : Double.NaN;
    }

    /**
     * Convenience delegator. Returns the Y coordinate of the cast origin.
     *
     * @return Y origin coordinate, or {@link Double#NaN} if not a location cast
     * @see LocationCastContext#y()
     */
    public double getY() {
        return context instanceof LocationCastContext loc ? loc.y() : Double.NaN;
    }

    /**
     * Convenience delegator. Returns the Z coordinate of the cast origin.
     *
     * @return Z origin coordinate, or {@link Double#NaN} if not a location cast
     * @see LocationCastContext#z()
     */
    public double getZ() {
        return context instanceof LocationCastContext loc ? loc.z() : Double.NaN;
    }

    /**
     * Convenience delegator. Returns the facing {@link Direction} of the cast origin.
     *
     * @return the cast {@link Direction}, or {@code null} if not a location cast
     * @see LocationCastContext#direction()
     */
    @Nullable
    public Direction getDirection() {
        return context instanceof LocationCastContext loc ? loc.direction() : null;
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.isCanceled = cancel;
    }

    /**
     * Identifies the origin type of spell cast. Implemented as an interface rather than an enum to allow addon mods to define
     * their own custom sources without modifying core wizardry code.
     * <p>
     * All built-in sources are available via {@link Sources}.
     *
     * @see Sources
     */
    public interface Source {
        /**
         * Returns a unique string identifier for this source.  (e.g. {@code "wand"}, {@code "my_addon_altar"})
         *
         * @return the source identifier string
         */
        String getIdentifier();
    }

    /** Built-in {@link Source} implementations covering all vanilla wizardry cast origins. */
    public enum Sources implements Source {
        WAND("wand"),
        SCROLL("scroll"),
        NPC("npc"),
        DISPENSER("dispenser"),
        OTHER("other"),
        COMMAND("command");

        private final String identifier;

        Sources(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String getIdentifier() {
            return identifier;
        }
    }

    /**
     * Fired <b>before</b> a spell cast begins, helps to cancel the cast or make changes to the {@link CastContext} (e.g. modifiers)
     */
    public static class Pre extends SpellCastEvent {

        /**
         * Creates a pre-cast event backed by a {@link CastContext}. Used for custom cast origins.
         *
         * @param source  the origin of the cast
         * @param spell   the spell about to be cast
         * @param context the full casting context
         */
        public Pre(Source source, Spell spell, CastContext context) {
            super(source, spell, context);
        }

        /** @deprecated Use {@link #Pre(Source, Spell, CastContext)} with an {@link EntityCastContext}. */
        @Deprecated(forRemoval = true)
        public Pre(Source source, Spell spell, LivingEntity caster, SpellModifiers modifiers) {
            super(source, spell, caster, modifiers);
        }

        /** @deprecated Use {@link #Pre(Source, Spell, CastContext)} with a {@link LocationCastContext}. */
        @Deprecated(forRemoval = true)
        public Pre(Source source, Spell spell, Level world, double x, double y, double z, Direction direction, SpellModifiers modifiers) {
            super(source, spell, world, x, y, z, direction, modifiers);
        }

        @Override
        public boolean canBeCanceled() {
            return true;
        }
    }

    /**
     * Fired <b>after</b> a spell has fully resolved, it can't be canceled, and it's only supposed to be used if you want to create
     * custom behaviors in the mod, not modifying the cast.
     */
    public static class Post extends SpellCastEvent {

        /**
         * Creates a post-cast event backed by a {@link CastContext}.
         *
         * @param source  the origin of the cast
         * @param spell   the spell that was cast
         * @param context the full casting context
         */
        public Post(Source source, Spell spell, CastContext context) {
            super(source, spell, context);
        }

        /** @deprecated Use {@link #Post(Source, Spell, CastContext)} with an {@link EntityCastContext}. */
        @Deprecated(forRemoval = true)
        public Post(Source source, Spell spell, LivingEntity caster, SpellModifiers modifiers) {
            super(source, spell, caster, modifiers);
        }

        /** @deprecated Use {@link #Post(Source, Spell, CastContext)} with a {@link LocationCastContext}. */
        @Deprecated(forRemoval = true)
        public Post(Source source, Spell spell, Level world, double x, double y, double z, Direction direction, SpellModifiers modifiers) {
            super(source, spell, world, x, y, z, direction, modifiers);
        }

        @Override
        public boolean canBeCanceled() {
            return false;
        }
    }

    /**
     * Fired <b>every game tick</b> while a spell is actively being charged or a continuous spell is active. This event can be
     * canceled to interrupt the cast.
     */
    public static class Tick extends SpellCastEvent {

        /**
         * Creates a tick event backed by a {@link CastContext}.
         *
         * @param source  the origin of the cast
         * @param spell   the spell being cast
         * @param context the full casting context (must have {@code castingTicks} set)
         */
        public Tick(Source source, Spell spell, CastContext context) {
            super(source, spell, context);
        }

        /** @deprecated Use {@link #Tick(Source, Spell, CastContext)} with an {@link EntityCastContext}.. */
        @Deprecated(forRemoval = true)
        public Tick(Source source, Spell spell, LivingEntity caster, SpellModifiers modifiers, int ticks) {
            super(source, spell, caster, modifiers);
        }

        /** @deprecated Use {@link #Tick(Source, Spell, CastContext)} with a {@link LocationCastContext}.. */
        @Deprecated(forRemoval = true)
        public Tick(Source source, Spell spell, Level world, double x, double y, double z, Direction direction, SpellModifiers modifiers, int ticks) {
            super(source, spell, world, x, y, z, direction, modifiers);
        }

        /**
         * @deprecated Use {@link CastContext#castingTicks()} via {@link #getContext()} instead:
         * <pre>{@code int ticks = event.getContext().castingTicks(); }</pre>
         */
        @Deprecated(forRemoval = true)
        public int getTicksCasting() {
            return getContext().castingTicks();
        }

        @Override
        public boolean canBeCanceled() {
            return true;
        }
    }

    /** Fired when a spell cast is completed. */
    public static class Finish extends SpellCastEvent {

        /**
         * Creates a finish event backed by a {@link CastContext}.
         *
         * @param source  the origin of the cast
         * @param spell   the spell that finished channeling
         * @param context the full casting context (must have {@code castingTicks} set)
         */
        public Finish(Source source, Spell spell, CastContext context) {
            super(source, spell, context);
        }

        /** @deprecated Use {@link #Finish(Source, Spell, CastContext)} with an {@link EntityCastContext}. */
        @Deprecated(forRemoval = true)
        public Finish(Source source, Spell spell, LivingEntity caster, SpellModifiers modifiers, int ticks) {
            super(source, spell, caster, modifiers);
        }

        /** @deprecated Use {@link #Finish(Source, Spell, CastContext)} with a {@link LocationCastContext}. */
        @Deprecated(forRemoval = true)
        public Finish(Source source, Spell spell, Level world, double x, double y, double z, Direction direction, SpellModifiers modifiers, int ticks) {
            super(source, spell, world, x, y, z, direction, modifiers);
        }

        /**
         * @deprecated Use {@link CastContext#castingTicks()} via {@link #getContext()}
         */
        @Deprecated(forRemoval = true)
        public int getTicksCasting() {
            return getContext().castingTicks();
        }

        @Override
        public boolean canBeCanceled() {
            return false;
        }
    }
}