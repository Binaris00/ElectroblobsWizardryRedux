package com.electroblob.wizardry.content.entity.goal;

import com.electroblob.wizardry.api.content.entity.living.ISpellCaster;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class AttackSpellGoal<T extends Mob & ISpellCaster> extends Goal {
    private final T attacker;
    private final int baseCooldown;
    private final int continuousSpellDuration;
    private final double speed;
    private final float maxAttackDistance;
    private LivingEntity target;
    private int cooldown;
    private int continuousSpellTimer;
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
        //TODO might be incorrect;
        attacker.setContinuousSpell(spell);
        //WizardryPacketHandler.net.send(PacketDistributor.ALL.noArg(), new PacketNPCCastSpell(attacker.getId(), target == null ? -1 : target.getId(), InteractionHand.MAIN_HAND, spell, modifiers));
    }

    @Override
    public void tick() {
        double distanceSq = this.attacker.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean targetIsVisible = this.attacker.getSensing().hasLineOfSight(this.target);

        if (targetIsVisible) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (distanceSq <= (double) this.maxAttackDistance && this.seeTime >= 20) {
            this.attacker.getNavigation().stop();
        } else {
            this.attacker.getNavigation().moveTo(this.target, this.speed);
        }

        this.attacker.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (this.continuousSpellTimer > 0) {
            this.continuousSpellTimer--;

            EntityCastContext ctx = new EntityCastContext(attacker.level(), attacker, InteractionHand.MAIN_HAND, this.continuousSpellDuration - this.continuousSpellTimer, target, attacker.getModifiers());

            if (distanceSq > (double) this.maxAttackDistance
                    || !targetIsVisible || WizardryEventBus.getInstance().fire(new SpellCastEvent.Tick(SpellCastEvent.Source.NPC, attacker.getContinuousSpell(), attacker, attacker.getModifiers(), this.continuousSpellDuration - this.continuousSpellTimer))
                    || !attacker.getContinuousSpell().cast(ctx)
                    || this.continuousSpellTimer == 0) {
                this.continuousSpellTimer = 0;
                this.cooldown = attacker.getContinuousSpell().getCooldown() + this.baseCooldown;
                setContinuousSpellAndNotify(Spells.NONE, new SpellModifiers());
                return;

            } else if (this.continuousSpellDuration - this.continuousSpellTimer == 1) {
                WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.NPC, attacker.getContinuousSpell(), attacker, attacker.getModifiers()));
            }

        } else if (--this.cooldown == 0) {
            if (distanceSq > (double) this.maxAttackDistance || !targetIsVisible) {
                return;
            }

            double dx = target.getX() - attacker.getX();
            double dz = target.getZ() - attacker.getZ();

            List<Spell> spells = new ArrayList<>(attacker.getSpells());

            if (!spells.isEmpty()) {
                if (!attacker.level().isClientSide) {
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
            }

        } else if (this.cooldown < 0) {
            this.cooldown = this.baseCooldown;
        }
    }

    private boolean attemptCastSpell(Spell spell, SpellModifiers modifiers) {
        if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(SpellCastEvent.Source.NPC, spell, attacker, modifiers))) {
            return false;
        }

        EntityCastContext ctx = new EntityCastContext(attacker.level(), attacker, InteractionHand.MAIN_HAND, 0, target, modifiers);
        if (spell.cast(ctx)) {
            if (!spell.isInstantCast()) {
                this.continuousSpellTimer = this.continuousSpellDuration - 1;
                setContinuousSpellAndNotify(spell, modifiers);

            } else {
                WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.NPC, spell, attacker, modifiers));

                this.cooldown = this.baseCooldown + spell.getCooldown();
                // TODO PACKETS
//                if (spell.requiresPacket()) {
//                    PacketNPCCastSpell msg = new PacketNPCCastSpell(attacker.getId(), target.getId(), InteractionHand.MAIN_HAND, spell, modifiers);
//                    WizardryPacketHandler.net.send(PacketDistributor.DIMENSION.with(() -> attacker.level.dimension()), msg);
//                }
            }

            return true;
        }

        return false;
    }
}
