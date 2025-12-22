package com.electroblob.wizardry.content.effect;

import com.electroblob.wizardry.api.content.effect.MagicMobEffect;
import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class FireSkinMobEffect extends MagicMobEffect {
    public FireSkinMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    public static void onLivingHurt(EBLivingHurtEvent event) {
        if (event.isCanceled()) return;

        Entity attacker = event.getSource().getEntity();
        if (attacker == null) return;

        if (event.getDamagedEntity().hasEffect(EBMobEffects.FIRESKIN.get()) &&
                !EBMagicDamageSource.isEntityImmune(EBDamageSources.FIRE, event.getDamagedEntity())) {
            attacker.setSecondsOnFire(Spells.FIRE_BREATH.property(DefaultProperties.EFFECT_DURATION) * 20);
        }
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        world.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        livingEntity.clearFire();
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return true;
    }
}
