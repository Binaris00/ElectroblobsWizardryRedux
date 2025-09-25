package com.electroblob.wizardry.content.entity.goal;

import com.electroblob.wizardry.content.entity.living.Wizard;
import com.electroblob.wizardry.core.AllyDesignation;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class MinionOwnerHurtByTarget extends TargetGoal {
    Mob minion;
    LivingEntity attacker;
    LivingEntity owner;
    private int timestamp;

    public MinionOwnerHurtByTarget(Mob minion) {
        super(minion, false);
        this.minion = minion;
    }

    @Override
    public boolean canUse() {
//        if (this.owner == null) {
//            if (!SummonedEntityData.isSummonedEntity(this.minion)) {
//                return false;
//            } else {
//                SummonedEntityData data = Services.WIZARD_DATA.getSummonedEntityData(this.minion);
//                this.owner = data.getSummoner();
//            }
//        }
//
//        if (this.owner != null) {
//            this.attacker = owner.getLastHurtByMob();
//            int i = owner.getLastHurtByMobTimestamp();
//            return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT) && isValidTarget(this.attacker);
//        }

        return false;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
        if (owner != null) {
            this.timestamp = owner.getLastHurtByMobTimestamp();
        }
        super.start();
    }


    private boolean isValidTarget(Entity target) {
        if (!AllyDesignation.isValidTarget(this.owner, target)) return false;

        if (target instanceof Player) {
            if (owner instanceof Wizard) {
                return owner.getLastAttacker() == target || ((Wizard) owner).getTarget() == target;
            }
            return true;
        }

        return target instanceof Monster || (target instanceof LivingEntity living && living.getLastAttacker() == this.owner);
    }
}
