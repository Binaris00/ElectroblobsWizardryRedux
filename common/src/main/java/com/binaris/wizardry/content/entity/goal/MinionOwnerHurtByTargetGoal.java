package com.binaris.wizardry.content.entity.goal;

import com.binaris.wizardry.api.content.data.MinionData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class MinionOwnerHurtByTargetGoal extends TargetGoal {
    private final MinionData data;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public MinionOwnerHurtByTargetGoal(Mob minion, MinionData data) {
        super(minion, false);
        this.data = data;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        if (!this.data.isSummoned()) return false;

        LivingEntity owner = data.getOwner();
        if (owner == null) {
            return false;
        }

        this.ownerLastHurtBy = owner.getLastHurtByMob();
        int i = owner.getLastHurtByMobTimestamp();
        return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity livingentity = this.data.getOwner();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
