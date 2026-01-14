package com.binaris.wizardry.content.spell.ice;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.core.EBConstants;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Permafrost extends RaySpell {
    public Permafrost() {
        this.particleVelocity(1);
        this.particleSpacing(0.5);
        soundValues(0.5f, 1, 0);
        this.ignoreLivingEntities(true);
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        boolean flag = false;
        if (!ctx.world().isClientSide) {
            int blastUpgradeCount = (int) ((ctx.modifiers().get(EBItems.BLAST_UPGRADE.get()) - 1) / EBConstants.BLAST_RADIUS_INCREASE_PER_LEVEL + 0.5f);
            float radius = 0.5f + 0.73f * blastUpgradeCount;
            int duration = (int) (property(DefaultProperties.DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get()));
            List<BlockPos> sphere = BlockUtil.getBlockSphere(blockHit.getBlockPos().above(), radius);
            for (BlockPos pos1 : sphere) {
                flag |= tryToPlaceIce(ctx.world(), pos1, ctx.caster(), duration);
            }
            return flag;
        }
        return true;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return true;
    }

    private boolean tryToPlaceIce(Level world, BlockPos pos, LivingEntity caster, int duration) {
        if (world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP) &&
                BlockUtil.canBlockBeReplaced(world, pos)) {
            if (BlockUtil.canPlaceBlock(caster, world, pos)) {
                world.setBlockAndUpdate(pos, EBBlocks.PERMAFROST.get().defaultBlockState());
                world.scheduleTick(pos.immutable(), EBBlocks.PERMAFROST.get(), duration);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected void playSound(Level world, LivingEntity entity, int castTicks, int duration) {
        this.playSoundLoop(world, entity, castTicks);
    }

    @Override
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
        this.playSoundLoop(world, x, y, z, ticksInUse, duration);
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        float brightness = ctx.world().random.nextFloat();
        ParticleBuilder.create(EBParticles.DUST).pos(x, y, z).velocity(vx, vy, vz).time(8 + ctx.world().random.nextInt(12))
                .color(0.4f + 0.6f * brightness, 0.6f + 0.4f * brightness, 1).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.SNOW).pos(x, y, z).velocity(vx, vy, vz).time(8 + ctx.world().random.nextInt(12)).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.ICE, SpellType.ALTERATION, SpellAction.POINT, 10, 10, 40)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.DAMAGE, 3F)
                .add(DefaultProperties.DURATION, 600)
                .add(DefaultProperties.EFFECT_DURATION, 100)
                .add(DefaultProperties.EFFECT_STRENGTH, 0)
                .build();
    }
}
