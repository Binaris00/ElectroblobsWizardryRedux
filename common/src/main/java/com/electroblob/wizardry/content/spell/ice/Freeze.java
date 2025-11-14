package com.electroblob.wizardry.content.spell.ice;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.*;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
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
                || EBMagicDamageSource.isEntityImmune(EBDamageSources.FROST, target)) return false;

        if (target instanceof Blaze || target instanceof MagmaCube) {
            DamageSource source = ctx.caster() != null ? EBMagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.FROST) : target.damageSources().magic();
            target.hurt(source, property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));
        }

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
