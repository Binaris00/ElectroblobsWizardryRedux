package com.electroblob.wizardry.common.content.spell.lightning;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.AreaEffectSpell;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BlindingFlash extends AreaEffectSpell {
    public BlindingFlash(){
        this.alwaysSucceed(true);
    }

    @Override
    protected boolean affectEntity(Level world, Vec3 origin, @Nullable LivingEntity caster, LivingEntity target, int targetCount, int ticksInUse) {
        if (EntityUtil.isLiving(target)) {
            int duration = 300;
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 0));
        }

        return true;
    }

    @Override
    protected void spawnParticleEffect(Level world, Vec3 origin, double radius, @Nullable LivingEntity caster) {
        if (caster != null) origin = origin.add(0, caster.getBbHeight() + 1, 0);
        // TODO Bin: Particle sphere crash?
        //ParticleBuilder.create(EBParticles.SPHERE).pos(origin).scale((float) radius * 0.8f).spawn(world);
    }
}
