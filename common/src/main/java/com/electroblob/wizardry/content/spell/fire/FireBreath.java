package com.electroblob.wizardry.content.spell.fire;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class FireBreath extends RaySpell {
    public FireBreath(){
        this.particleVelocity(1);
        this.particleJitter(0.3);
        this.particleSpacing(0.25);
        this.soundValues(3f, 1, 0);
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if(!EntityUtil.canDamageBlocks(ctx.caster(), ctx.world())) return false;
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection());

        if (ctx.world().isEmptyBlock(pos) && BlockUtil.canPlaceBlock(ctx.caster(), ctx.world(), pos)) {
            if(!ctx.world().isClientSide) ctx.world().setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
            return true;
        }

        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity target && !EBMagicDamageSource.isEntityImmune(EBDamageSources.FIRE, target)) {
            if (ctx.ticksInUse() % target.invulnerableDuration == 1) {
                target.setSecondsOnFire(10);
                DamageSource source = ctx.caster() != null ? EBMagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.FIRE)
                        : target.damageSources().magic();
                EntityUtil.attackEntityWithoutKnockback(target, source, 5);
            }
        }

        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return true;
    }

    @Override
    protected void playSound(Level world, LivingEntity entity, int ticksInUse, int duration) {
        this.playSoundLoop(world, entity, ticksInUse);
    }

    @Override
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
        this.playSoundLoop(world, x, y, z, ticksInUse, duration);
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).scale(2 + ctx.world().random.nextFloat()).collide(true).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).scale(2 + ctx.world().random.nextFloat()).collide(true).spawn(ctx.world());
    }

    // TODO PROPERTIES
    @Override
    protected SpellProperties properties() {
        return null;
    }
}
