package com.electroblob.wizardry.content.spell.fire;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.MagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Detonate extends RaySpell {
    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (ctx.world().isClientSide) {
            ctx.world().addParticle(ParticleTypes.EXPLOSION_EMITTER, blockHit.getBlockPos().getX() + 0.5, blockHit.getBlockPos().getY() + 0.5, blockHit.getBlockPos().getZ() + 0.5, 0, 0, 0);
            ctx.world().playSound(null, blockHit.getBlockPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE);
            return true;
        }

        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(this.property(DefaultProperties.BLAST_RADIUS) * ctx.modifiers().get(EBItems.BLAST_UPGRADE.get()),
                blockHit.getBlockPos().getX(), blockHit.getBlockPos().getY(), blockHit.getBlockPos().getZ(), ctx.world());

        for (LivingEntity target : targets) {
            DamageSource source = ctx.caster() != null ? MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.BLAST)
                    : target.damageSources().magic();

            target.hurt(source, Math.max(property(DefaultProperties.DAMAGE) -
                    (float) target.distanceToSqr(blockHit.getBlockPos().getX() + 0.5, blockHit.getBlockPos().getY() + 0.5,
                            blockHit.getBlockPos().getZ() + 0.5) * 4, 0) * ctx.modifiers().get(SpellModifiers.POTENCY));
        }
        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ctx.world().addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.FIRE, SpellType.ATTACK, SpellAction.POINT, 45, 0, 50)
                .add(DefaultProperties.RANGE, 16F)
                .add(DefaultProperties.DAMAGE, 12F)
                .add(DefaultProperties.BLAST_RADIUS, 3F)
                .build();
    }
}
