package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.effect.CurseMobEffect;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class RemoveCurse extends BuffSpell {
    public RemoveCurse() {
        super(1, 1, 0.3f);
    }

    @Override
    protected boolean applyEffects(LivingEntity caster) {
        if(!caster.getActiveEffects().isEmpty()){
            boolean flag = false;
            for(MobEffectInstance effect : caster.getActiveEffects()){
                if(effect.getEffect() instanceof CurseMobEffect){
                    caster.removeEffect(effect.getEffect());
                    flag = true;
                }
            }

            return flag;
        }

        return false;
    }

    @Override
    protected void spawnParticles(Level world, LivingEntity caster) {
        super.spawnParticles(world, caster);

        for(int i = 0; i < particleCount*2; i++){
            double x = caster.xo + world.random.nextDouble() * 2 - 1;
            double y = caster.yo + caster.getEyeHeight() - 0.5 + world.random.nextDouble();
            double z = caster.zo + world.random.nextDouble() * 2 - 1;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.14, 0).color(0x0f001b)
                    .time(20 + world.random.nextInt(12)).spawn(world);
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0x0f001b).spawn(world);
        }
    }
}
