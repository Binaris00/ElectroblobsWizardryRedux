package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Event fired when a player is about to discover a spell.
 * <p>
 * This event is fired on the {@link com.binaris.wizardry.core.event.WizardryEventBus WizardryEventBus}.
 * Since it extends {@link WizardryCancelableEvent}, it is cancelable. If the event is canceled, 
 * the spell discovery will be prevented (e.g., the spell will not be marked as discovered, 
 * and associated items like scrolls will not be consumed/used).
 * </p>
 */
public class DiscoverSpellEvent extends WizardryCancelableEvent {
    private final Player player;
    private final Spell spell;
    private final Source source;

    /**
     * Constructs a new {@code DiscoverSpellEvent}.
     *
     * @param player The player discovering the spell.
     * @param spell  The spell being discovered.
     * @param source The source or method of the discovery.
     */
    public DiscoverSpellEvent(Player player, Spell spell, Source source) {
        this.player = player;
        this.spell = spell;
        this.source = source;
    }

    /**
     * Gets the spell that is being discovered.
     *
     * @return The {@link Spell} being discovered.
     */
    public Spell getSpell() {
        return spell;
    }

    /**
     * Gets the source of the spell discovery.
     *
     * @return The {@link Sources} describing how the spell was discovered.
     */
    public Source getSource() {
        return source;
    }

    /**
     * Gets the player who is discovering the spell.
     *
     * @return The {@link Player} discovering the spell.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Interface representing the source of a spell discovery.
     * Can be implemented to define custom discovery sources.
     */
    public interface Source {
        /**
         * Gets the unique name of this discovery source.
         *
         * @return The name string.
         */
        String getName();
    }

    /** Built-in sources of spell discovery. */
    public enum Sources implements Source {
        /** Fired when a spell is discovered by casting it (e.g., from a wand or scroll). */
        CASTING("casting"),
        /** Fired when a spell is discovered using an Identification Scroll on an undiscovered spell scroll or book. */
        IDENTIFICATION_SCROLL("identification_scroll"),
        /** Fired when a spell is discovered via a command. (/discover) */
        COMMAND("command"),
        /** Fired when a spell is discovered by purchasing a spell book from a Wizard. */
        PURCHASE("purchase"),
        /** Fired when a spell is discovered through other miscellaneous means. */
        OTHER("other");

        final String name;

        Sources(String name) {
            this.name = name;
        }

        /**
         * Finds a built-in source enum value by its name.
         *
         * @param name The name of the source (case-sensitive).
         * @return The matching {@link Sources} value, or {@code null} if no match is found.
         */
        @Nullable
        public static Sources byName(String name) {
            for (Sources source : values()) {
                if (source.getName().equals(name)) return source;
            }
            return null;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
