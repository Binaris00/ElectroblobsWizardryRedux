package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.Tiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Poison extends RaySpell {
    public Poison() {
        this.particleVelocity(-0.5);
        this.particleSpacing(0.4);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if(entityHit.getEntity() instanceof LivingEntity livingTarget && !EBMagicDamageSource.isEntityImmune(EBDamageSources.POISON, livingTarget)){
            DamageSource source = ctx.caster() != null ? EBMagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.POISON)
                    : livingTarget.damageSources().magic();

            livingTarget.hurt(source, property(DefaultProperties.DAMAGE));
            livingTarget.addEffect(new MobEffectInstance(MobEffects.POISON,
                    property(DefaultProperties.EFFECT_DURATION),
                    property(DefaultProperties.EFFECT_STRENGTH)));
        }
        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.3f, 0.7f, 0).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8)).color(0.1f, 0.4f, 0).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(Tiers.APPRENTICE, Elements.EARTH, SpellType.ATTACK, SpellAction.POINT, 10, 0, 20)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.DAMAGE, 1F)
                .add(DefaultProperties.EFFECT_DURATION, 200)
                .add(DefaultProperties.EFFECT_STRENGTH, 1)
                .build();
    }
}
