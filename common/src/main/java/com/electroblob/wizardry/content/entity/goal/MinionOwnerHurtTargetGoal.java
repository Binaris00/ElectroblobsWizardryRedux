package com.electroblob.wizardry.content.entity.goal;

import com.electroblob.wizardry.api.content.data.MinionData;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class MinionOwnerHurtTargetGoal extends TargetGoal {
    private final Mob minion;
    private final MinionData data;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public MinionOwnerHurtTargetGoal(Mob mob) {
        super(mob, false);
        if (!Services.OBJECT_DATA.isMinion(mob))
            throw new RuntimeException("MinionOwnerHurtByTargetGoal can only be used by minions!");
        this.minion = mob;
        this.data = Services.OBJECT_DATA.getMinionData(minion);
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
