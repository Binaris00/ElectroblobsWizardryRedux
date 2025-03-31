package com.electroblob.wizardry.common.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reversal extends RaySpell {
    @Override
    protected boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse) {
        return true;
    }

    @Override
    protected boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        return false;
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if (caster != null && target instanceof LivingEntity) {
            List<MobEffectInstance> negativePotions = new ArrayList<>(caster.getActiveEffects());
            negativePotions.removeIf(p -> !p.getEffect().getCategory().equals(MobEffectCategory.HARMFUL));

            if (!world.isClientSide) {
                if (negativePotions.isEmpty()) return false;

                // TODO BIN, no bonus effects :c
                int bonusEffects = 0;
                int n = property(DefaultProperties.EFFECT_STRENGTH) + bonusEffects;

                Collections.shuffle(negativePotions);
                negativePotions = negativePotions.subList(0, Math.min(negativePotions.size(), n));

                negativePotions.forEach(p -> caster.removeEffect(p.getEffect()));
                negativePotions.forEach(((LivingEntity) target)::addEffect);

            } else {
                ParticleBuilder.create(EBParticles.BUFF).entity(caster).color(1, 1, 0.3f).spawn(world);
            }
        }

        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(world);
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + world.random.nextInt(8)).color(0.1f, 0, 0.05f).spawn(world);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 8F)
                .add(DefaultProperties.EFFECT_STRENGTH, 1)
                .build();
    }
}
