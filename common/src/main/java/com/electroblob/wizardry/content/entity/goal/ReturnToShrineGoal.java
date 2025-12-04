package com.electroblob.wizardry.content.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;
import java.util.function.Supplier;

/**
 * Goal that makes an Evil Wizard teleport back to a shrine position if they get too far away.
 * This prevents wizards from wandering off and leaving players trapped in containment.
 */
public class ReturnToShrineGoal extends Goal {
    private final Mob mob;
    private final Supplier<BlockPos> shrinePositionSupplier;
    private final LevelReader level;
    private final PathNavigation navigation;
    private final float maxDistance;
    private final float teleportDistance;
    private int checkCooldown;

    /**
     * @param mob The mob that should return to the shrine
     * @param shrinePositionSupplier Supplier that provides the shrine position (can be null if not in a shrine event)
     * @param maxDistance Maximum distance before the mob starts pathfinding back
     * @param teleportDistance Distance at which the mob teleports back instead of pathfinding
     */
    public ReturnToShrineGoal(Mob mob, Supplier<BlockPos> shrinePositionSupplier, float maxDistance, float teleportDistance) {
        this.mob = mob;
        this.shrinePositionSupplier = shrinePositionSupplier;
        this.level = mob.level();
        this.navigation = mob.getNavigation();
        this.maxDistance = maxDistance;
        this.teleportDistance = teleportDistance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        BlockPos shrinePos = shrinePositionSupplier.get();
        if (shrinePos == null) return false; // Not part of a shrine event
        
        if (this.mob.isPassenger() || this.mob.isLeashed()) return false;
        
        double distanceSq = this.mob.distanceToSqr(shrinePos.getX() + 0.5, shrinePos.getY(), shrinePos.getZ() + 0.5);
        return distanceSq > (double) (this.maxDistance * this.maxDistance);
    }

    @Override
    public boolean canContinueToUse() {
        BlockPos shrinePos = shrinePositionSupplier.get();
        if (shrinePos == null) return false;
        
        if (this.navigation.isDone()) return false;
        if (this.mob.isPassenger() || this.mob.isLeashed()) return false;
        
        double distanceSq = this.mob.distanceToSqr(shrinePos.getX() + 0.5, shrinePos.getY(), shrinePos.getZ() + 0.5);
        return distanceSq > (this.maxDistance * this.maxDistance * 0.25); // Stop when closer
    }

    @Override
    public void start() {
        this.checkCooldown = 0;
    }

    @Override
    public void stop() {
        this.navigation.stop();
    }

    @Override
    public void tick() {
        BlockPos shrinePos = shrinePositionSupplier.get();
        if (shrinePos == null) return;

        if (--this.checkCooldown <= 0) {
            this.checkCooldown = this.adjustedTickDelay(10);
            
            double distanceSq = this.mob.distanceToSqr(shrinePos.getX() + 0.5, shrinePos.getY(), shrinePos.getZ() + 0.5);
            
            // If too far, teleport back
            if (distanceSq >= (double) (this.teleportDistance * this.teleportDistance)) {
                this.teleportToShrine(shrinePos);
            } else {
                // Otherwise, pathfind back
                this.navigation.moveTo(shrinePos.getX() + 0.5, shrinePos.getY(), shrinePos.getZ() + 0.5, 1.0);
            }
        }
    }

    private void teleportToShrine(BlockPos shrinePos) {
        // Try to find a valid teleport position around the shrine
        for (int i = 0; i < 10; ++i) {
            int x = shrinePos.getX() + this.randomIntInclusive(-5, 5);
            int y = shrinePos.getY() + this.randomIntInclusive(-1, 2);
            int z = shrinePos.getZ() + this.randomIntInclusive(-5, 5);
            
            if (this.maybeTeleportTo(x, y, z)) {
                return;
            }
        }
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        
        if (!this.canTeleportTo(pos)) {
            return false;
        }
        
        this.mob.moveTo((double) x + 0.5, y, (double) z + 0.5, this.mob.getYRot(), this.mob.getXRot());
        this.navigation.stop();
        return true;
    }

    private boolean canTeleportTo(BlockPos pos) {
        BlockPathTypes blockPathType = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
        if (blockPathType != BlockPathTypes.WALKABLE) {
            return false;
        }
        
        BlockState blockBelow = this.level.getBlockState(pos.below());
        if (blockBelow.getBlock() instanceof LeavesBlock) {
            return false;
        }
        
        BlockPos offset = pos.subtract(this.mob.blockPosition());
        return this.level.noCollision(this.mob, this.mob.getBoundingBox().move(offset));
    }

    private int randomIntInclusive(int min, int max) {
        return this.mob.getRandom().nextInt(max - min + 1) + min;
    }
}
