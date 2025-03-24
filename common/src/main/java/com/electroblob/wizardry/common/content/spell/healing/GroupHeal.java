package com.electroblob.wizardry.common.content.spell.healing;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.common.content.spell.abstr.AreaEffectSpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GroupHeal extends AreaEffectSpell {
    public GroupHeal(){
        this.targetAllies(true);
    }

    @Override
    protected boolean affectEntity(Level world, Vec3 origin, @Nullable LivingEntity caster, LivingEntity target, int targetCount, int ticksInUse) {
        if(target.getHealth() < target.getMaxHealth() && target.getHealth() > 0){

            Heal.heal(target, 6);

            if(world.isClientSide) ParticleBuilder.spawnHealParticles(world, target);
            playSound(caster.level(), target, 0, -1);
            return true;
        }

        return false;
    }

    @Override
    protected void spawnParticleEffect(Level world, Vec3 origin, double radius, @Nullable LivingEntity caster) {

    }
}
