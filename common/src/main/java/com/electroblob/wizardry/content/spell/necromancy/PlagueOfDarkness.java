package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.AreaEffectSpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PlagueOfDarkness extends AreaEffectSpell {
    @Override
    protected boolean affectEntity(Level world, Vec3 origin, @Nullable LivingEntity caster, LivingEntity target, int targetCount, int ticksInUse) {
        if(!EBMagicDamageSource.isEntityImmune(EBDamageSources.WITHER, target)) {
            target.hurt(target.damageSources().wither(), property(DefaultProperties.DAMAGE));
            target.addEffect(new MobEffectInstance(MobEffects.WITHER,
                    property(DefaultProperties.EFFECT_DURATION),
                    property(DefaultProperties.EFFECT_STRENGTH)));
        }

        return true;
    }

    @Override
    protected void spawnParticleEffect(Level world, Vec3 origin, double radius, @Nullable LivingEntity caster) {
        double particleX, particleZ;

        for(int i = 0; i < 40; i++){

            particleX = origin.x - 1.0d + 2 * world.random.nextDouble();
            particleZ = origin.z - 1.0d + 2 * world.random.nextDouble();
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(particleX, origin.y, particleZ)
                    .velocity(particleX - origin.x, 0, particleZ - origin.z).color(0.1f, 0, 0).spawn(world);

            particleX = origin.x - 1.0d + 2 * world.random.nextDouble();
            particleZ = origin.z - 1.0d + 2 * world.random.nextDouble();
            ParticleBuilder.create(EBParticles.SPARKLE).pos(particleX, origin.y, particleZ)
                    .velocity(particleX - origin.x, 0, particleZ - origin.z).time(30).color(0.1f, 0, 0.05f).spawn(world);

            particleX = origin.x - 1.0d + 2 * world.random.nextDouble();
            particleZ = origin.z - 1.0d + 2 * world.random.nextDouble();

            BlockState state = world.getBlockState(new BlockPos((int) origin.x, (int) (origin.y - 0.5), (int) origin.z));

            if(state.getRenderShape() != RenderShape.INVISIBLE) {
                world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), particleX, origin.y, particleZ, particleX - origin.x, 0, particleZ - origin.z);
            }
        }

        ParticleBuilder.create(EBParticles.SPHERE).pos(origin.add(0, 0.1, 0)).scale((float)radius * 0.8f).color(0.8f, 0, 0.05f).spawn(world);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.DAMAGE, 8F)
                .add(DefaultProperties.EFFECT_DURATION, 140)
                .add(DefaultProperties.EFFECT_STRENGTH, 2)
                .build();
    }
}
