package com.electroblob.wizardry.common.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.common.content.spell.abstr.AreaEffectSpell;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PlagueOfDarkness extends AreaEffectSpell {
    @Override
    protected boolean affectEntity(Level world, Vec3 origin, @Nullable LivingEntity caster, LivingEntity target, int targetCount, int ticksInUse) {
        // TODO !MagicDamage.isEntityImmune(DamageType.WITHER, target)

        target.hurt(target.damageSources().wither(), 8);
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 140, 2));

        return true;
    }

    @Override
    protected SpellProperties properties() {
        return null;
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
}
