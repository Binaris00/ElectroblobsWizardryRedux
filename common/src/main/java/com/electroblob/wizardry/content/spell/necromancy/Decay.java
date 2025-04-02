package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.content.entity.construct.DecayConstruct;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ConstructRangedSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Decay extends ConstructRangedSpell<DecayConstruct> {
    public static final SpellProperty<Integer> PATCHES_SPAWNED = SpellProperty.intProperty("decay_patches_spawned", 5);

    public Decay() {
        super(DecayConstruct::new, false);
    }

    @Override
    protected boolean spawnConstruct(Level world, double x, double y, double z, @Nullable Direction side, @Nullable LivingEntity caster) {
        BlockPos origin = BlockPos.containing(x, y, z);
        if (world.getBlockState(origin).isCollisionShapeFullBlock(world, origin)) return false;

        super.spawnConstruct(world, x, y, z, side, caster);

        float decayCount = property(PATCHES_SPAWNED);
        int quantity = (int) (decayCount * 1);
        int horizontalRange = (int) (0.4 * decayCount * 1);
        int verticalRange = 6;

        for (int i = 0; i < quantity; i++) {
            BlockPos pos = BlockUtil.findNearbyFloorSpace(world, origin, horizontalRange, verticalRange, false);
            if (pos == null) break;
            super.spawnConstruct(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, side, caster);
        }

        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 12F)
                .add(DefaultProperties.DURATION, 400)
                .add(DefaultProperties.EFFECT_DURATION, 400)
                .add(PATCHES_SPAWNED)
                .build();
    }
}
