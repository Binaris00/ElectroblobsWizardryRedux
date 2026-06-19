package com.binaris.wizardry.content.effect;

import com.binaris.wizardry.api.content.effect.CurseMobEffect;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.core.MobEffectContext;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.binaris.wizardry.setup.registries.EBSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.Random;

public class ArcaneJammerMobEffect extends CurseMobEffect {
    /** Random number generator used to coordinate whether spellcasting works or not. */
    private static final Random random = new Random();
    /** The number of ticks between updates of whether spellcasting works or not. */
    private static final int UPDATE_INTERVAL = 15;

    public ArcaneJammerMobEffect() {
        super(MobEffectCategory.HARMFUL, 0);
    }

    @Override
    public void onSpellPreCast(SpellCastEvent.Pre event, MobEffectContext context) {
        if (event.getCaster() == null) {
            return;
        }
        random.setSeed(event.getLevel().getGameTime() / UPDATE_INTERVAL);
        random.nextInt(2);

        if(random.nextInt(event.getCaster().getEffect(EBMobEffects.ARCANE_JAMMER.get()).getAmplifier() + 2) > 0){
            event.setCanceled(true);

            event.getLevel().playSound(event.getCaster(), event.getCaster().blockPosition(), EBSounds.MISC_SPELL_FAIL.get(), SoundSource.MASTER, 1.0F, 1.0F);

            if (event.getLevel().isClientSide) return;
            for(int i = 0; i < 5; i++){
                double x = event.getCaster().xo + 0.5f * (event.getLevel().random.nextFloat() - 0.5f);
                double y = event.getCaster().yo + event.getCaster().getBbHeight() / 2 + 0.5f * (event.getLevel().random.nextFloat() - 0.5f);
                double z = event.getCaster().zo + 0.5f * (event.getLevel().random.nextFloat() - 0.5f);
                ((ServerLevel) event.getLevel()).sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 1, 0.5, 0.5, 0.5, 0);
            }
        }
    }
}
