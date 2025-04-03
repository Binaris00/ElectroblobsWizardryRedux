package com.electroblob.wizardry.content.effect;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.effect.MagicMobEffect;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.EBLivingHurtEvent;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.Level;

public class StaticAuraMobEffect extends MagicMobEffect {
    public StaticAuraMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        ParticleBuilder.create(EBParticles.SPARK).pos(x, y, z).spawn(world);
    }

    public static void onLivingHurt(EBLivingHurtEvent event) {
        if(event.isCancelled()) return;
        DamageSource source = event.getSource();

        if(source.getEntity() != null && event.getDamagedEntity().hasEffect(EBMobEffects.STATIC_AURA.get())){
            source.getEntity().hurt(EBMagicDamageSource.causeDirectMagicDamage(event.getDamagedEntity(), EBDamageSources.SHOCK),
                    event.getAmount() / 2);
            source.getEntity().playSound(EBSounds.SPELL_STATIC_AURA_RETALIATE.get(), 1.0F,
                    event.getDamagedEntity().level().random.nextFloat() * 0.4F + 1.5F);
        }
    }
}
