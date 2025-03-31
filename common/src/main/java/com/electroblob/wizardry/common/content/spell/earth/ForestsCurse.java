package com.electroblob.wizardry.common.content.spell.earth;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.common.content.spell.abstr.AreaEffectSpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ForestsCurse extends AreaEffectSpell {
    public ForestsCurse(){
        this.alwaysSucceed(true);
        this.soundValues(1, 1.1f, 0.2f);
    }

    @Override
    protected boolean affectEntity(Level world, Vec3 origin, @Nullable LivingEntity caster, LivingEntity target, int targetCount, int ticksInUse) {
        if(!EBMagicDamageSource.isEntityImmune(EBDamageSources.POISON, target)){
            DamageSource source = caster != null ? EBMagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.POISON)
                    : target.damageSources().magic();
            target.hurt(source, property(DefaultProperties.DAMAGE));

            int duration = property(DefaultProperties.EFFECT_DURATION);
            int amplifier = property(DefaultProperties.EFFECT_STRENGTH);

            target.addEffect(new MobEffectInstance(MobEffects.POISON, duration, amplifier));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, amplifier));
        }

        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z) {
        y += 2; // Moves the particles up to the caster's head level

        float brightness = world.random.nextFloat() / 4;
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).velocity(0, -0.2, 0)
                .color(0.05f + brightness, 0.2f + brightness, 0).spawn(world);

        brightness = world.random.nextFloat() / 4;
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, -0.05, 0).time(50)
                .color(0.1f + brightness, 0.2f + brightness, 0).spawn(world);

        ParticleBuilder.create(EBParticles.LEAF).pos(x, y, z).velocity(0, -0.01, 0).time(40 + world.random.nextInt(12)).spawn(world);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.DAMAGE, 4F)
                .add(DefaultProperties.EFFECT_DURATION, 140)
                .add(DefaultProperties.EFFECT_STRENGTH, 2)
                .build();
    }
}
