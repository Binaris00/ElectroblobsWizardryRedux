package com.electroblob.wizardry.common.content.spell.ice;

import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.common.util.BlockUtil;
import com.electroblob.wizardry.api.common.util.GeometryUtil;
import com.electroblob.wizardry.common.content.entity.construct.EntityIceSpike;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.common.content.spell.abstr.ConstructRangedSpell;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class IceSpickes extends ConstructRangedSpell<EntityIceSpike> {
    public IceSpickes() {
        super(EntityIceSpike::new, true);
        this.ignoreUncollidables(true);
    }

    @Override
    protected boolean spawnConstruct(Level world, double x, double y, double z, @Nullable Direction side, @Nullable LivingEntity caster) {
        if (side == null) return false;

        BlockPos blockHit = BlockPos.containing(x, y, z);
        if (side.getAxisDirection() == Direction.AxisDirection.NEGATIVE) blockHit = blockHit.relative(side);

        if (world.getBlockState(blockHit).isCollisionShapeFullBlock(world, blockHit)) return false;

        Vec3 origin = new Vec3(x, y, z);

        Vec3 pos = origin.add(new Vec3(side.getOpposite().step()));

        super.spawnConstruct(world, pos.x, pos.y, pos.z, side, caster);

        int quantity = Spells.ICE_SPICKES.property(DefaultProperties.ENTITIES);
        float maxRadius = Spells.ICE_SPICKES.property(DefaultProperties.EFFECT_RADIUS);

        for (int i = 0; i < quantity; i++) {
            double radius = 0.5 + world.random.nextDouble() * (maxRadius - 0.5);

            Vec3 offset = Vec3.directionFromRotation(world.random.nextFloat() * 180 - 90, world.random.nextBoolean() ? 0 : 180)
                    .scale(radius).yRot(side.toYRot() * (float) Math.PI / 180).xRot(GeometryUtil.getPitch(side) * (float) Math.PI / 180);

            if (side.getAxis().isHorizontal()) offset = offset.yRot((float) Math.PI / 2);

            Integer surface = BlockUtil.getNearestSurface(world, new BlockPos(BlockPos.containing(origin.add(offset))), side,
                    (int) maxRadius, true, BlockUtil.SurfaceCriteria.basedOn(this::isCollisionShapeFullBlock));

            if (surface != null) {
                Vec3 vec = GeometryUtil.replaceComponent(origin.add(offset), side.getAxis(), surface).subtract(new Vec3(side.step()));
                super.spawnConstruct(world, vec.x, vec.y, vec.z, side, caster);
            }
        }

        return true;
    }

    public boolean isCollisionShapeFullBlock(BlockGetter p_60839_, BlockPos p_60840_) {
        return p_60839_.getBlockState(p_60840_).isCollisionShapeFullBlock(p_60839_, p_60840_);
    }

    @Override
    protected void addConstructExtras(EntityIceSpike construct, Direction side, @Nullable LivingEntity caster) {
        construct.lifetime = 30 + construct.level().random.nextInt(15);
        construct.setFacing(side);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 20F)
                .add(DefaultProperties.EFFECT_RADIUS, 3)
                .add(DefaultProperties.ENTITIES, 18)
                .add(DefaultProperties.DAMAGE, 5F)
                .add(DefaultProperties.EFFECT_DURATION, 100)
                .add(DefaultProperties.EFFECT_STRENGTH, 0)
                .build();
    }
}
