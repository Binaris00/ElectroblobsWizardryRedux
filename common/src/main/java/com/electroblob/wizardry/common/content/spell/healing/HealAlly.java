package com.electroblob.wizardry.common.content.spell.healing;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HealAlly extends RaySpell {
    @Override
    protected boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse) {
        return false;
    }

    @Override
    protected boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        return false;
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if (target instanceof LivingEntity livingTarget) {

            if (livingTarget.getHealth() < livingTarget.getMaxHealth() && livingTarget.getHealth() > 0) {
                livingTarget.heal(property(Heal.HEALTH));

                if (world.isClientSide) ParticleBuilder.spawnHealParticles(world, livingTarget);
                playSound(world, livingTarget, 0, -1);
            }

            return true;
        }

        return false;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 10F)
                .add(Heal.HEALTH, 5F)
                .build();
    }
}
