package com.electroblob.wizardry.content.spell.fire;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Ignite extends RaySpell {
    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        BlockPos blockPos = blockHit.getBlockPos().relative(blockHit.getDirection());
        if (ctx.world().isEmptyBlock(blockPos)) {
            if (!ctx.world().isClientSide && BlockUtil.canPlaceBlock(ctx.caster(), ctx.world(), blockPos))
                ctx.world().setBlockAndUpdate(blockPos, Blocks.FIRE.defaultBlockState());
            return true;
        }
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity target && !EBMagicDamageSource.isEntityImmune(EBDamageSources.FIRE, target)) {
            target.setSecondsOnFire((int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())));
            return true;
        }

        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ctx.world().addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.FIRE, SpellType.ATTACK, SpellAction.POINT, 5, 0, 10)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.EFFECT_DURATION, 10)
                .build();
    }
}
