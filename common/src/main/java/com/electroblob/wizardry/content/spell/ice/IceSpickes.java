package com.electroblob.wizardry.content.spell.ice;

import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.GeometryUtil;
import com.electroblob.wizardry.content.entity.construct.IceSpikeConstruct;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ConstructRangedSpell;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class IceSpickes extends ConstructRangedSpell<IceSpikeConstruct> {
    public IceSpickes() {
        super(IceSpikeConstruct::new, true);
        this.ignoreUncollidables(true);
    }

    @Override
    protected boolean spawnConstruct(CastContext ctx, Vec3 origin, @Nullable Direction side) {
        if (side == null) return false;

        BlockPos blockHit = BlockPos.containing(origin);
        if (side.getAxisDirection() == Direction.AxisDirection.NEGATIVE) blockHit = blockHit.relative(side);
        if (ctx.world().getBlockState(blockHit).isCollisionShapeFullBlock(ctx.world(), blockHit)) return false;

        Vec3 pos = origin.add(new Vec3(side.getOpposite().step()));
        super.spawnConstruct(ctx, pos, side);

        int quantity = Spells.ICE_SPICKES.property(DefaultProperties.ENTITIES);
        float maxRadius = Spells.ICE_SPICKES.property(DefaultProperties.EFFECT_RADIUS);
        for (int i = 0; i < quantity; i++) {
            double radius = 0.5 + ctx.world().random.nextDouble() * (maxRadius - 0.5);

            Vec3 offset = Vec3.directionFromRotation(ctx.world().random.nextFloat() * 180 - 90, ctx.world().random.nextBoolean() ? 0 : 180)
                    .scale(radius).yRot(side.toYRot() * (float) Math.PI / 180).xRot(GeometryUtil.getPitch(side) * (float) Math.PI / 180);

            if (side.getAxis().isHorizontal()) offset = offset.yRot((float) Math.PI / 2);

            Integer surface = BlockUtil.getNearestSurface(ctx.world(), new BlockPos(BlockPos.containing(origin.add(offset))), side,
                    (int) maxRadius, true, BlockUtil.SurfaceCriteria.basedOn(this::isCollisionShapeFullBlock));

            if (surface != null) {
                Vec3 vec = GeometryUtil.replaceComponent(origin.add(offset), side.getAxis(), surface).subtract(new Vec3(side.step()));
                super.spawnConstruct(ctx, vec, side);
            }
        }

        return true;
    }

    public boolean isCollisionShapeFullBlock(BlockGetter blockGetter, BlockPos pos) {
        return blockGetter.getBlockState(pos).isCollisionShapeFullBlock(blockGetter, pos);
    }

    @Override
    protected void addConstructExtras(IceSpikeConstruct construct, Direction side, @Nullable LivingEntity caster) {
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
