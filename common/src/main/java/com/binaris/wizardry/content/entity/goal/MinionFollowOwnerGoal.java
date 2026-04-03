package com.binaris.wizardry.content.entity.goal;

import com.binaris.wizardry.api.content.data.MinionData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

public class MinionFollowOwnerGoal extends Goal {
    private static final float SPEED_MODIFIER = 1.0F;
    private static final float STOP_DISTANCE = 2.0F;
    private static final float START_DISTANCE = 10.0F;

    private final Mob minion;
    private final MinionData data;
    private final LevelReader level;
    private boolean canFly = false;
    private final PathNavigation navigation;
    private LivingEntity owner;
    private float oldWaterCost;
    private int timeToRecalcPath;

    public MinionFollowOwnerGoal(Mob minion, MinionData data, boolean canFly){
        this(minion, data);
        this.canFly = canFly;
    }

    public MinionFollowOwnerGoal(Mob minion, MinionData data) {
        this.minion = minion;
        this.data = data;
        this.level = minion.level();
        this.navigation = minion.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));

        if (!(minion.getNavigation() instanceof GroundPathNavigation) && !(minion.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for MinionFollowOwnerGoal");
        }
    }

    public boolean canUse() {
        LivingEntity livingentity = this.data.getOwner();
        if (livingentity == null) {
            return false;
        } else if (livingentity.isSpectator()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else if (this.minion.distanceToSqr(livingentity) < (double) (this.START_DISTANCE * this.START_DISTANCE)) {
            return false;
        } else {
            this.owner = livingentity;
            return true;
        }
    }

    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else {
            return !(this.minion.distanceToSqr(this.owner) <= (double) (this.STOP_DISTANCE * this.STOP_DISTANCE));
        }
    }

    private boolean unableToMove() {
        return this.minion.isPassenger() || this.minion.isLeashed();
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.minion.getPathfindingMalus(BlockPathTypes.WATER);
        this.minion.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.minion.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    public void tick() {
        this.minion.getLookControl().setLookAt(this.owner, 10.0F, (float) this.minion.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (this.minion.distanceToSqr(this.owner) >= (double) 144.0F) {
                this.teleportToOwner();
            } else {
                this.navigation.moveTo(this.owner, this.SPEED_MODIFIER);
            }
        }

    }

    private void teleportToOwner() {
        BlockPos blockpos = this.owner.blockPosition();

        for (int i = 0; i < 10; ++i) {
            int j = this.randomIntInclusive(-3, 3);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-3, 3);
            boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }

    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.minion.moveTo((double) x + 0.5D, y, (double) z + 0.5D, this.minion.getYRot(), this.minion.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
        if (blockpathtypes != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = this.level.getBlockState(pos.below());
            if (!this.canFly && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(this.minion.blockPosition());
                return this.level.noCollision(this.minion, this.minion.getBoundingBox().move(blockpos));
            }
        }
    }

    private int randomIntInclusive(int min, int max) {
        return this.minion.getRandom().nextInt(max - min + 1) + min;
    }
}