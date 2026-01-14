package com.binaris.wizardry.content.spell.ice;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Freeze extends RaySpell {
    public Freeze() {
        this.soundValues(1, 1.4f, 0.4f);
        this.hitLiquids(true);
        this.ignoreUncollidables(false);
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!ctx.world().isClientSide && BlockUtil.canPlaceBlock(ctx.caster(), ctx.world(), blockHit.getBlockPos())) {
            BlockUtil.freeze(ctx.world(), blockHit.getBlockPos(), true);
        }

        return true;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)
                || MagicDamageSource.isEntityImmune(EBDamageSources.FROST, target)) return false;

        if (target instanceof Blaze || target instanceof MagmaCube) {
            DamageSource source = ctx.caster() != null ? MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.FROST) : target.damageSources().magic();
            target.hurt(source, property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));
        }

        if (ctx.world().isClientSide) return true;
        target.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(),
                (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())),
                property(DefaultProperties.EFFECT_STRENGTH)));
        if (target.isOnFire()) target.clearFire();
        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        float brightness = 0.5f + (ctx.world().random.nextFloat() / 2);
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8))
                .color(brightness, brightness + 0.1f, 1).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.SNOW).pos(x, y, z).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.ICE, SpellType.ATTACK, SpellAction.POINT, 5, 0, 10)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.DAMAGE, 3f)
                .add(DefaultProperties.EFFECT_DURATION, 200)
                .add(DefaultProperties.EFFECT_STRENGTH, 1)
                .build();
    }
}
