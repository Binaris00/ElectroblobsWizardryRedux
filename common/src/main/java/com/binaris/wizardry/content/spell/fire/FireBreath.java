package com.binaris.wizardry.content.spell.fire;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FireBreath extends RaySpell {
    public FireBreath() {
        this.particleVelocity(1);
        this.particleJitter(0.3);
        this.particleSpacing(0.25);
        this.soundValues(3f, 1, 0);
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!EntityUtil.canDamageBlocks(ctx.caster(), ctx.world())) return false;
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection());

        if (ctx.world().isEmptyBlock(pos) && BlockUtil.canPlaceBlock(ctx.caster(), ctx.world(), pos)) {
            if (!ctx.world().isClientSide) ctx.world().setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
            return true;
        }

        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity target && !MagicDamageSource.isEntityImmune(EBDamageSources.FIRE, target)) {
            if (ctx.castingTicks() % target.invulnerableDuration == 1) {
                target.setSecondsOnFire((int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())));
                DamageSource source = ctx.caster() != null ? MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.FIRE)
                        : target.damageSources().magic();
                EntityUtil.attackEntityWithoutKnockback(target, source, property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));
            }
        }

        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return true;
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
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).scale(2 + ctx.world().random.nextFloat()).collide(true).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).scale(2 + ctx.world().random.nextFloat()).collide(true).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.FIRE, SpellType.ATTACK, SpellAction.POINT, 15, 15, 70)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.DAMAGE, 6F)
                .add(DefaultProperties.EFFECT_DURATION, 10)
                .build();
    }
}
