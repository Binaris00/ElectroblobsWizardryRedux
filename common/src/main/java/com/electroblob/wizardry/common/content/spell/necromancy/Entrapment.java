package com.electroblob.wizardry.common.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.common.content.entity.construct.BubbleConstruct;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Entrapment extends RaySpell {
    public Entrapment(){
        this.soundValues(1, 0.85f, 0.3f);
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if (target instanceof LivingEntity) {
            if (!world.isClientSide) {
                target.hurt(target.damageSources().magic(), 1);

                BubbleConstruct bubble = new BubbleConstruct(world);
                bubble.setPos(target.getX(), target.getY(), target.getZ());
                bubble.setCaster((Player) caster);
                bubble.lifetime = 200;
                bubble.isDarkOrb = true;
                bubble.damageMultiplier = 1;

                world.addFreshEntity(bubble);
                target.startRiding(bubble);
            }
        }

        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        world.addParticle(ParticleTypes.PORTAL, x, y - 0.5, z, 0, 0, 0);
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(world);
    }

    @Override
    protected boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse) {
        return false;
    }

    @Override
    protected boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
