package com.binaris.wizardry.content.entity.goal;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.mixin.accessor.BlazeAccessor;
import com.binaris.wizardry.core.networking.s2c.NPCSpellCastS2C;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Blaze;

import java.util.EnumSet;

public class BlazeLikeSpellAttackGoal extends Goal {

    private final Blaze blaze;
    private final Spell spell;

    private int attackStep;
    private int attackTime;
    private int lastSeen;

    public BlazeLikeSpellAttackGoal(Blaze blaze, Spell spell) {
        this.blaze = blaze;
        this.spell = spell;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = blaze.getTarget();
        return target != null && target.isAlive() && blaze.canAttack(target);
    }

    @Override
    public void start() {
        attackStep = 0;
    }

    @Override
    public void stop() {
        ((BlazeAccessor) blaze).callSetCharged(false);
        lastSeen = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        attackTime--;
        LivingEntity target = blaze.getTarget();
        if (target == null) return;

        boolean hasSight = blaze.getSensing().hasLineOfSight(target);
        lastSeen = hasSight ? 0 : lastSeen + 1;

        double distanceSq = blaze.distanceToSqr(target);
        double followDistSq = getFollowDistance() * getFollowDistance();

        if (distanceSq < 4.0) {
            if (!hasSight) return;
            if (attackTime <= 0) {
                attackTime = 20;
                blaze.doHurtTarget(target);
            }
            blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
            super.tick();
            return;
        }

        if (distanceSq < followDistSq && hasSight) {
            if (attackTime <= 0) {
                attackStep++;
                if (attackStep == 1) {
                    attackTime = 60;
                    ((BlazeAccessor) blaze).callSetCharged(true);
                } else if (attackStep <= 4) {
                    attackTime = 6;
                } else {
                    attackTime = 100;
                    attackStep = 0;
                    ((BlazeAccessor) blaze).callSetCharged(false);
                }

                if (attackStep > 1) {
                    attemptCast(target);
                }
            }
            blaze.getLookControl().setLookAt(target, 10.0F, 10.0F);
            super.tick();
            return;
        }

        if (lastSeen < 5) {
            blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
        }

        super.tick();
    }

    private void attemptCast(LivingEntity target) {
        SpellModifiers modifiers = new SpellModifiers();
        EntityCastContext ctx = new EntityCastContext(blaze.level(), blaze, InteractionHand.MAIN_HAND, 0, target, modifiers);
        spell.cast(ctx);

        WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.NPC, spell, blaze, modifiers));

        if (!blaze.level().isClientSide && spell.requiresPacket()) {
            NPCSpellCastS2C msg = new NPCSpellCastS2C(blaze.getId(), target.getId(), InteractionHand.MAIN_HAND, spell, modifiers);
            Services.NETWORK_HELPER.sendToTracking(blaze, msg);
        }
    }

    private double getFollowDistance() {
        return blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
    }
}
