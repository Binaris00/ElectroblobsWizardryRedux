package com.electroblob.wizardry.common.core;

import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import net.minecraft.server.level.ServerLevel;

import java.util.LinkedList;

/**
 * SpellEngine is designed to optimize spell casting by minimizing overhead and improving performance.
 *
 * The engine utilizes a LinkedList to efficiently manage the addition and removal of spells during their casting process.
 * The LinkedList allows for arbitrary creation and deletion of elements in O(1) time, ensuring minimal performance impact
 * during frequent updates.
 *
 * One of the key improvements in this engine is the detachment of spell casting from the caster (entity). In previous versions
 * (1.20.1), spell casting was directly tied to the caster, which could mislead profiler tools, such as Sparky, into attributing
 * performance issues to the entity itself. When casting logic was executed in the entity tick method, it could cause the profiler
 * to incorrectly show high tick times, masking the true cause: the spell's casting process.
 *
 * By decoupling spell casting from the entity tick, this engine ensures that the casting logic runs separately, preventing
 * delays in the entity’s tick time and allowing the entity's updates to remain unaffected by the spells it casts.
 *
 * Additionally, the SpellEngine provides a centralized location for accessing all currently active spells, making it easier
 * to manage and track ongoing spell casts throughout the mod.
 *
 * @author 19
 */
public final class SpellEngine {

    // TODO: Make per server level the engine is ticking for different dimensions
    private final LinkedList<QueuedSpell> activeSpellCastings = new LinkedList<>();

    // Pass the level in case we need it for something which we probably will.
    public static void run(ServerLevel level) {
        var this_ = Instance.INSTANCE;

        this_.activeSpellCastings.removeIf(live -> live.spell.hasEnded());

        for (QueuedSpell live : this_.activeSpellCastings) {
            live.spell.updateCast(live.caster);
        }
    }

    public static void addSpellToLevel(Spell spell, Caster caster) {
        Instance.INSTANCE.activeSpellCastings.add(new QueuedSpell(spell, caster));
    }

    private record QueuedSpell(Spell spell, Caster caster){}

    private static class Instance {
        private static final SpellEngine INSTANCE = new SpellEngine();
    }

    private SpellEngine() {}
}
