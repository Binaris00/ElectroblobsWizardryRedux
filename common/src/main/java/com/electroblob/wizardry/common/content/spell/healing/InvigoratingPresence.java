package com.electroblob.wizardry.common.content.spell.healing;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.common.content.spell.abstr.AreaEffectSpell;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class InvigoratingPresence extends AreaEffectSpell {
    public InvigoratingPresence(){
        this.soundValues(0.7f, 1.2f, 0.4f);
        this.alwaysSucceed(true);
        this.targetAllies(true);
    }

    @Override
    protected boolean affectEntity(Level world, Vec3 origin, @Nullable LivingEntity caster, LivingEntity target, int targetCount, int ticksInUse) {
        // TODO ? SpellBuff.getStandardBonusAmplifier(modifiers.get(SpellModifiers.POTENCY)
        target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                property(DefaultProperties.EFFECT_DURATION),
                property(DefaultProperties.EFFECT_STRENGTH)));

        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z){
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.03, 0).time(50)
                .color(1, 0.2f, 0.2f).spawn(world);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.EFFECT_DURATION, 900)
                .add(DefaultProperties.EFFECT_STRENGTH, 1)
                .build();
    }
}
