package com.binaris.wizardry.content.effect;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.Level;

public class StaticAuraMobEffect extends MagicMobEffect {
    public StaticAuraMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    public static void onLivingHurt(EBLivingHurtEvent event) {
        if (event.isCanceled()) return;
        DamageSource source = event.getSource();

        if (source.getEntity() != null && event.getDamagedEntity().hasEffect(EBMobEffects.STATIC_AURA.get())) {
            source.getEntity().hurt(MagicDamageSource.causeDirectMagicDamage(event.getDamagedEntity(), EBDamageSources.SHOCK),
                    event.getAmount() / 2);
            source.getEntity().playSound(EBSounds.SPELL_STATIC_AURA_RETALIATE.get(), 1.0F,
                    event.getDamagedEntity().level().random.nextFloat() * 0.4F + 1.5F);
        }
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        ParticleBuilder.create(EBParticles.SPARK).pos(x, y, z).spawn(world);
    }
}
