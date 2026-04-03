package com.binaris.wizardry.content.entity.goal;

import com.binaris.wizardry.api.content.data.MinionData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class MinionOwnerHurtTargetGoal extends TargetGoal {
    private final MinionData data;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public MinionOwnerHurtTargetGoal(Mob mob, MinionData data) {
        super(mob, false);
        this.data = data;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (!this.data.isSummoned()) return false;

        LivingEntity livingentity = this.data.getOwner();
        if (livingentity == null) {
            return false;
        }

        this.ownerLastHurt = livingentity.getLastHurtMob();
        int i = livingentity.getLastHurtMobTimestamp();
        return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT);
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity livingentity = this.data.getOwner();
        if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
