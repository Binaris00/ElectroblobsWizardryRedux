package com.binaris.wizardry.content.entity.goal;

import com.binaris.wizardry.api.content.entity.living.ISpellCaster;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.content.entity.living.AbstractWizard;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.networking.s2c.NPCSpellCastS2C;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Basic version of {@link AttackSpellGoal} that can be used by most spell-casting mobs without checking movement or look
 * controls, leaving those to other goals. This class handles spell selection, cooldowns, and casting logic. It
 * attempts to cast a random spell from the mob's spell list when the cooldown expires, provided the target is within range
 * and visible. It supports both instant and continuous spells.
 */
public class AttackSpellBasicGoal<T extends Mob & ISpellCaster> extends Goal {
    /** The mob that will use this goal */
    private final T attacker;

    /** Base cooldown between spell casts */
    private final int baseCooldown;

    /** Duration for continuous spells */
    private final int continuousSpellDuration;

    /** Maximum attack distance squared */
    private final float maxAttackDistance;

    /** The current target entity */
    private LivingEntity target;

    /** Current cooldown timer */
    private int cooldown;

    /** Timer for continuous spell casting */
    private int continuousSpellTimer;

    /** Time the target has been visible */
    private int seeTime;

    /**
     * Default constructor.
     *
     * @param attacker                Mob that will use this goal
     * @param maxDistance             Maximum distance to the target for casting spells
     * @param baseCooldown            Base cooldown between spell casts
     * @param continuousSpellDuration Duration for continuous spells
     */
    public AttackSpellBasicGoal(T attacker, float maxDistance, int baseCooldown, int continuousSpellDuration) {
        this.cooldown = -1;
        this.attacker = attacker;
        this.baseCooldown = baseCooldown;
        this.continuousSpellDuration = continuousSpellDuration;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.attacker.getTarget();
        if (livingEntity == null) return false;

        this.target = livingEntity;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.cooldown = -1;
        this.setContinuousSpellAndNotify(Spells.NONE, new SpellModifiers());
        this.continuousSpellTimer = 0;
    }

    /**
     * Sets the continuous spell for the attacker and notifies tracking clients.
     */
    private void setContinuousSpellAndNotify(Spell spell, SpellModifiers modifiers) {
        attacker.setContinuousSpell(spell);
        Services.NETWORK_HELPER.sendToTracking(attacker,
                new NPCSpellCastS2C(attacker.getId(), target == null ? -1 : target.getId(), InteractionHand.MAIN_HAND, spell, modifiers));
    }

    @Override
    public void tick() {
        if (this.target == null) {
            return;
        }

        double distanceSq = this.attacker.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean targetIsVisible = this.attacker.getSensing().hasLineOfSight(this.target);

        if (targetIsVisible) ++this.seeTime;
        else this.seeTime = 0;

        if (this.continuousSpellTimer > 0) {
            this.continuousSpellTimer--;

            int currentTick = this.continuousSpellDuration - this.continuousSpellTimer;
            EntityCastContext ctx = new EntityCastContext(attacker.level(), attacker, InteractionHand.MAIN_HAND, currentTick, target, attacker.getModifiers());

            attacker.setSpellCounter(currentTick);

            // Conditions to stop casting the continuous spell
            if (distanceSq > (double) this.maxAttackDistance
                    || !targetIsVisible
                    || WizardryEventBus.getInstance().fire(new SpellCastEvent.Tick(SpellCastEvent.Source.NPC, attacker.getContinuousSpell(), attacker, attacker.getModifiers(), currentTick))
                    || !attacker.getContinuousSpell().cast(ctx)
                    || this.continuousSpellTimer == 0) {

                this.continuousSpellTimer = 0;
                this.cooldown = attacker.getContinuousSpell().getCooldown() + this.baseCooldown;
                setContinuousSpellAndNotify(Spells.NONE, new SpellModifiers());
                attacker.setSpellCounter(0);

            } else if (currentTick == 1) {
                WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.NPC, attacker.getContinuousSpell(), attacker, attacker.getModifiers()));
            }

        } else if (--this.cooldown == 0) {
            if (distanceSq > (double) this.maxAttackDistance || !targetIsVisible || this.seeTime < 5) {
                this.cooldown = 10;
                return;
            }

            List<Spell> spells = new ArrayList<>(attacker.getSpells());

            if (!spells.isEmpty() && !attacker.level().isClientSide) {
                Spell spell;

                while (!spells.isEmpty()) {
                    spell = spells.get(attacker.level().random.nextInt(spells.size()));

                    SpellModifiers modifiers = attacker.getModifiers();

                    if (spell != null && attemptCastSpell(spell, modifiers)) {
                        return;
                    } else {
                        spells.remove(spell);
                    }
                }
            }

            // If no spell was cast, reset the cooldown
            this.cooldown = this.baseCooldown;

        } else if (this.cooldown < 0) {
            this.cooldown = this.baseCooldown;
        }
    }

    /**
     * Try to cast the given spell with the specified modifiers, handling pre-cast and post-cast events.
     *
     * @param spell     The spell to cast.
     * @param modifiers The spell modifiers to apply.
     */
    private boolean attemptCastSpell(Spell spell, SpellModifiers modifiers) {
        if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(SpellCastEvent.Source.NPC, spell, attacker, modifiers)))
            return false;

        EntityCastContext ctx = new EntityCastContext(attacker.level(), attacker, InteractionHand.MAIN_HAND, 0, target, modifiers);

        if (!spell.cast(ctx)) {
            return false;
        }

        // Handle instant and continuous spells
        if (spell.isInstantCast()) {
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.NPC, spell, attacker, modifiers));
            this.cooldown = this.baseCooldown + spell.getCooldown();

            if (!attacker.level().isClientSide && spell.requiresPacket()) {
                NPCSpellCastS2C msg = new NPCSpellCastS2C(attacker.getId(), target.getId(), InteractionHand.MAIN_HAND, spell, modifiers);
                Services.NETWORK_HELPER.sendToTracking(attacker, msg);
            }

        } else {
            // Start casting continuous spell
            this.continuousSpellTimer = this.continuousSpellDuration - 1;
            setContinuousSpellAndNotify(spell, modifiers);
            attacker.setTarget(target);
            if (attacker instanceof AbstractWizard wizard) wizard.setSpellTargetId(target.getId());
        }

        return true;
    }
}