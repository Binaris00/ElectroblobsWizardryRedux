package com.electroblob.wizardry.core.mixin.accessor;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Mob.class)
public interface MobGoalsAccessor {
    @Accessor
    GoalSelector getGoalSelector();

    @Accessor
    GoalSelector getTargetSelector();

    @Accessor
    Map<BlockPathTypes, Float> getPathfindingMalus();
}
