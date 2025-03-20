package com.electroblob.wizardry.common.content.spell.necromancy;

import com.electroblob.wizardry.api.common.util.BlockUtil;
import com.electroblob.wizardry.common.content.entity.construct.DecayConstruct;
import com.electroblob.wizardry.common.content.spell.abstr.ConstructRangedSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Decay extends ConstructRangedSpell<DecayConstruct> {
    public Decay() {
        super(DecayConstruct::new, false);
    }

    @Override
    protected boolean spawnConstruct(Level world, double x, double y, double z, @Nullable Direction side, @Nullable LivingEntity caster) {
        BlockPos origin = BlockPos.containing(x, y, z);
        if (world.getBlockState(origin).isCollisionShapeFullBlock(world, origin)) return false;

        super.spawnConstruct(world, x, y, z, side, caster);

        float decayCount = 5;
        int quantity = (int) (decayCount * 1);
        int horizontalRange = (int) (0.4 * decayCount * 1);
        int verticalRange = (int) (6 * 1);

        for (int i = 0; i < quantity; i++) {
            BlockPos pos = BlockUtil.findNearbyFloorSpace(world, origin, horizontalRange, verticalRange, false);
            if (pos == null) break;
            super.spawnConstruct(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, side, caster);
        }

        return true;
    }
}
