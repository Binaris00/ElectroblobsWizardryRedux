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
 * A goal which allows a spell-casting mob to attack its target with spells. The mob will move towards the target
 * until it is within a specified distance and has line of sight, at which point it will stop and cast spells at it.
 * It can cast both instant and continuous spells.
 * <p>
 * For reference (specially related to continuous spells), check {@link AbstractWizard}
 * for an example of how to implement the {@link ISpellCaster} and handle spell casting properly.
 *
 * @param <T> The type of mob that is also a spell caster.
 */
public class AttackSpellGoal<T extends Mob & ISpellCaster> extends Goal {

    /** The mob that is casting the spell that's also implementing {@code ISpellCaster}. */
    private final T attacker;

    /** The base cooldown between spell casts, in ticks (not including spell-specific cooldowns). */
    private final int baseCooldown;

    /** The duration for which continuous spells are cast, in ticks. */
    private final int continuousSpellDuration;

    /** The movement speed of the mob towards its target. */
    private final double speed;

    /** The maximum distance at which the mob can cast spells, squared. */
    private final float maxAttackDistance;

    /** The current target of the mob. {@link AttackSpellGoal#attacker} */
    private LivingEntity target;

    /** The current cooldown before the next spell can be cast, in ticks. */
    private int cooldown;

    /** The timer for continuous spell casting, in ticks. */
    private int continuousSpellTimer;

    /** The number of ticks that are needed to see the target before casting. */
    private int seeTime;

    public AttackSpellGoal(T attacker, double speed, float maxDistance, int baseCooldown, int continuousSpellDuration) {
        this.cooldown = -1;
        this.attacker = attacker;
        this.baseCooldown = baseCooldown;
        this.continuousSpellDuration = continuousSpellDuration;
        this.speed = speed;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.attacker.getTarget();

        if (livingEntity == null) {
            return false;
        } else {
            this.target = livingEntity;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || !this.attacker.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.cooldown = -1;
        this.setContinuousSpellAndNotify(Spells.NONE, new SpellModifiers());
        this.continuousSpellTimer = 0;
    }

    private void setContinuousSpellAndNotify(Spell spell, SpellModifiers modifiers) {
        attacker.setContinuousSpell(spell);

        Services.NETWORK_HELPER.sendToTracking(attacker,
                new NPCSpellCastS2C(attacker.getId(), target == null ? -1 : target.getId(), InteractionHand.MAIN_HAND, spell, modifiers));
    }

    @Override
    public void tick() {
        double distanceSq = this.attacker.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean targetIsVisible = this.attacker.getSensing().hasLineOfSight(this.target);

        if (targetIsVisible) ++this.seeTime;
        else this.seeTime = 0;

        if (distanceSq <= (double) this.maxAttackDistance && this.seeTime >= 5) this.attacker.getNavigation().stop();
        else this.attacker.getNavigation().moveTo(this.target, this.speed);

        this.attacker.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (this.continuousSpellTimer > 0) {
            this.continuousSpellTimer--;

            int currentTick = this.continuousSpellDuration - this.continuousSpellTimer;
            EntityCastContext ctx = new EntityCastContext(attacker.level(), attacker, InteractionHand.MAIN_HAND, currentTick, target, attacker.getModifiers());

            // Update spell counter on server to sync with clients
            attacker.setSpellCounter(currentTick);

            if (distanceSq > (double) this.maxAttackDistance
                    || !targetIsVisible || WizardryEventBus.getInstance().fire(new SpellCastEvent.Tick(SpellCastEvent.Source.NPC, attacker.getContinuousSpell(), attacker, attacker.getModifiers(), currentTick))
                    || !attacker.getContinuousSpell().cast(ctx)
                    || this.continuousSpellTimer == 0) {
                this.continuousSpellTimer = 0;
                this.cooldown = attacker.getContinuousSpell().getCooldown() + this.baseCooldown;
                setContinuousSpellAndNotify(Spells.NONE, new SpellModifiers());
                attacker.setSpellCounter(0);
                return;

            } else if (currentTick == 1) {
                WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.NPC, attacker.getContinuousSpell(), attacker, attacker.getModifiers()));
            }

        } else if (--this.cooldown == 0) {
            // Check if target is in range and visible before attempting to cast
            if (distanceSq > (double) this.maxAttackDistance || !targetIsVisible) {
                // Reset cooldown to try again soon instead of waiting forever
                this.cooldown = 10; // Try again in 10 ticks
                return;
            }

            double dx = target.getX() - attacker.getX();
            double dz = target.getZ() - attacker.getZ();

            List<Spell> spells = new ArrayList<>(attacker.getSpells());

            if (!spells.isEmpty() && !attacker.level().isClientSide) {
                Spell spell;

                while (!spells.isEmpty()) {
                    spell = spells.get(attacker.level().random.nextInt(spells.size()));

                    SpellModifiers modifiers = attacker.getModifiers();

                    if (spell != null && attemptCastSpell(spell, modifiers)) {
                        attacker.setYRot((float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F);
                        return;
                    } else {
                        spells.remove(spell);
                    }
                }
            }

            // If we reach here, no spell was cast successfully, reset cooldown
            this.cooldown = this.baseCooldown;

        } else if (this.cooldown < 0) {
            this.cooldown = this.baseCooldown;
        }
    }

    private boolean attemptCastSpell(Spell spell, SpellModifiers modifiers) {
        if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(SpellCastEvent.Source.NPC, spell, attacker, modifiers)))
            return false;

        EntityCastContext ctx = new EntityCastContext(attacker.level(), attacker, InteractionHand.MAIN_HAND, 0, target, modifiers);

        if (!spell.cast(ctx)) return false;

        // Send spell cast packet and post cast event to clients for instant spells
        if (spell.isInstantCast()) {
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.NPC, spell, attacker, modifiers));
            this.cooldown = this.baseCooldown + spell.getCooldown();

            if (!attacker.level().isClientSide && spell.requiresPacket()) {
                NPCSpellCastS2C msg = new NPCSpellCastS2C(attacker.getId(), target.getId(), InteractionHand.MAIN_HAND, spell, modifiers);
                Services.NETWORK_HELPER.sendToTracking(attacker, msg);
            }

        } else {
            // Start continuous spell casting if applicable
            this.continuousSpellTimer = this.continuousSpellDuration - 1;
            setContinuousSpellAndNotify(spell, modifiers);
            // FIXME - this could be better, the target isn't available on client (aiStep) so we're saving the entity ID here
            attacker.setTarget(target);
            if (attacker instanceof AbstractWizard wizard) wizard.setSpellTargetId(target.getId());
        }

        return true;

    }
}
