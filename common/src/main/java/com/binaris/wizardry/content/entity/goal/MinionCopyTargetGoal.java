package com.binaris.wizardry.content.entity.goal;

import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Goal that helps to copy the owner's target for the minion. This is useful when the owner is a mob, allowing to quickly
 * copy the target of the owner and helping it to attack the same target as the owner.
 * <p>
 * This checks if the minion is actually summoned and if the owner is a mob, then it checks if the mob owner has a target
 * and if that target can be attacked by the minion.
 */
public class MinionCopyTargetGoal extends TargetGoal {
    private static final int RECHECK_DELAY_TICKS = 20;

    private final MinionData data;
    private final @Nullable LivingEntity owner;
    private int recheckDelay = 0;

    public MinionCopyTargetGoal(Mob mob) {
        super(mob, false);
        if (!Services.OBJECT_DATA.isMinion(mob))
            throw new RuntimeException("MinionCopyTargetGoal can only be used by minions!");
        this.data = Services.OBJECT_DATA.getMinionData(mob);
        this.owner = data.getOwner();
    }

    @Override
    public boolean canContinueToUse() {
        if (owner == null || owner.isDeadOrDying()) return false;
        if (owner instanceof Mob mobOwner) return mobOwner.getTarget() == this.mob.getTarget();
        return false;
    }

    public boolean canUse() {
        if (!this.data.isSummoned()) return false;
        if (owner instanceof Player) return false;

        if (recheckDelay-- > 0) return false;
        recheckDelay = RECHECK_DELAY_TICKS;

        if (owner instanceof Mob mobOwner) {
            LivingEntity target = mobOwner.getTarget();
            return target != null && this.canAttack(target, TargetingConditions.DEFAULT);
        }
        return false;
    }

    @Override
    public void start() {
        if (owner instanceof Mob mobOwner) {
            this.mob.setTarget(mobOwner.getTarget());
        }

        super.start();
    }
}
